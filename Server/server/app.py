import json
import random

from flask import Flask, jsonify, request
import uuid
import sqlite3
from crypto.PublicKey import RSA
from crypto.Signature import PKCS1_v1_5
from crypto.Hash import SHA256
import base64

# Initialize Flask app
app = Flask(__name__)

DB_FILE = 'database/server.db'
DB_SCHEMA = 'database/schema.sql'


# Initialize database
def init_db():
    try:
        print('Initializing database')
        conn = sqlite3.connect(DB_FILE)
        with open(DB_SCHEMA) as f:
            conn.executescript(f.read())
        print('Database initialized')
    except Exception as e:
        print('Error initializing database: {}'.format(e))
    finally:
        if conn:
            conn.close()


def get_db():
    try:
        conn = sqlite3.connect(DB_FILE)
        conn.row_factory = sqlite3.Row
        return conn, conn.cursor()
    except Exception as e:
        print('Error getting database: {}'.format(e))
        return None, None


# Register User route
@app.route('/register_user', methods=['POST'])
def register_user():
    try:
        # Get data from request
        data = request.json
        name = data.get('name')
        username = data.get('username')
        password = data.get('password')
        tax_number = data.get('tax_number')
        public_key = data.get('public_key')
        credit_card_number = data.get('credit_card_number')
        credit_card_validity = data.get('credit_card_validity')
        credit_card_type = data.get('credit_card_type')

        # Check if all required fields are present
        if not name or not username or not password or not tax_number or not public_key or not credit_card_number or not credit_card_validity or not credit_card_type:
            return jsonify({'error': 'Missing required fields'}), 400

        # Check if customer already exists
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM customer WHERE username = ?', (username,))
        customer = cursor.fetchone()
        if customer:
            # Return customer exists message
            return jsonify({'message': 'Customer already exists'}), 409

        customer_id = str(uuid.uuid4())
        # Insert new customer
        cursor.execute(
            'INSERT INTO customer (CUSTOMER_ID, USERNAME, PASSWORD, TAX_NUMBER, PUBLIC_KEY, NAME) VALUES (?, ?, ?, ?, ?, ?)',
            (customer_id, username, password, tax_number, public_key, name))

        # Insert new credit card
        cursor.execute('INSERT INTO credit_card (CUSTOMER_ID, TYPE, NUMBER, VALIDITY) VALUES (?, ?, ?, ?)',
                       (customer_id, credit_card_type, credit_card_number, credit_card_validity))
        conn.commit()

        return jsonify({
            'message': 'Customer registered successfully',
            'customer_id': customer_id
        }), 201
    except Exception as e:
        return jsonify({'error': 'Error registering customer: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()


# Login Route
@app.route('/login', methods=['POST'])
def login():
    try:
        # Get data from request
        data = request.json
        username = data.get('username')
        password = data.get('password')

        # Check if all required fields are present
        if not username or not password:
            return jsonify({'error': 'Missing required fields'}), 400

        # Check if customer exists
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM customer WHERE username = ? AND password = ?', (username, password))
        customer = cursor.fetchone()

        if not customer:
            return jsonify({'error': 'Invalid username or password'}), 401

        return jsonify({'message': 'Login successful'}), 200
    except Exception as e:
        return jsonify({'error': 'Error logging in: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()


# Next events Route
@app.route('/next_events', methods=['GET'])
def next_events():
    try:
        args = request.args

        nr_of_events = args.get('nr_of_events')

        # if no events parameter is passed
        if not nr_of_events:
            return jsonify({'error': 'Missing required fields'}), 400

        # Query next nr_of_events
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM EVENT as e where e.DATE >= datetime() limit ?', nr_of_events)
        events = cursor.fetchall()

        # If no events are found
        if not events:
            return jsonify({'message': 'No events found'}), 404

        return jsonify([dict(event) for event in events]), 200
    except Exception as e:
        return jsonify({'error': 'Error getting next events: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()


# Get event Route
@app.route('/event', methods=['GET'])
def get_event():
    try:
        args = request.args

        event_id = args.get('event_id')

        # if no event_id parameter is passed
        if not event_id:
            return jsonify({'error': 'Missing required fields'}), 400

        # Query event
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM EVENT as e where e.EVENT_ID = ?', event_id)
        event = cursor.fetchone()

        # If no event is found
        if not event:
            return jsonify({'message': 'Event not found'}), 404

        return jsonify(dict(event)), 200
    except Exception as e:
        return jsonify({'error': 'Error getting event: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()


# Buy Ticket Route
@app.route('/buy_ticket', methods=['POST'])
def buy_ticket():
    try:
        # Get data from request
        data = request.json
        customer_id = data.get('customer_id')
        event_id = data.get('event_id')
        nr_of_tickets = data.get('nr_of_tickets')
        signature = data.get('signature')

        # Check if all required fields are present
        if not customer_id or not event_id or not nr_of_tickets or not signature:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get public key of customer
        conn, cursor = get_db()
        cursor.execute('SELECT PUBLIC_KEY FROM customer WHERE CUSTOMER_ID = ?', (customer_id,))
        customer = cursor.fetchone()

        # if customer not found
        if not customer:
            return jsonify({'error': 'Customer not found'}), 404

        # Create public key from string
        public_key = RSA.importKey(customer['PUBLIC_KEY'])

        # Create a json object with data
        data = {
            'customer_id': customer_id,
            'event_id': event_id,
            'nr_of_tickets': nr_of_tickets
        }

        message = json.dumps(data, sort_keys=True)
        hash_object = SHA256.new(message.encode())
        verifier = PKCS1_v1_5.new(public_key)
        if not verifier.verify(hash_object, base64.b64decode(signature)):
            return jsonify({'error': 'Invalid signature'}), 401

        # Get ticket details
        cursor.execute('SELECT * FROM EVENT WHERE EVENT_ID = ?', (event_id,))
        event = cursor.fetchone()
        if not event:
            return jsonify({'error': 'Event not found'}), 404

        # Calculate total price
        total_price = event['PRICE'] * nr_of_tickets

        # Calculate total price of past purchases for the customer
        cursor.execute('SELECT SUM(TOTAL_PRICE) FROM PURCHASE WHERE CUSTOMER_ID = ?', (customer_id,))
        past_purchases = cursor.fetchone()
        if not past_purchases:
            past_purchases = 0
        else:
            past_purchases = past_purchases['SUM(TOTAL_PRICE)']

        # Insert new purchase
        cursor.execute('INSERT INTO PURCHASE (CUSTOMER_ID, DATE, TOTAL_PRICE) VALUES (?, datetime(), ?)',
                       (customer_id, total_price))

        # Get new purchase id
        purchase_id = cursor.lastrowid
        # Get date of last purchase
        cursor.execute('SELECT DATE FROM PURCHASE WHERE PURCHASE_ID = ?', (purchase_id,))
        date = cursor.fetchone()['DATE']

        created_tickets = []
        created_vouchers = []

        # Insert new tickets
        for i in range(nr_of_tickets):
            # Get the highest place number for the event
            cursor.execute('SELECT MAX(PLACE) FROM TICKET WHERE EVENT_ID = ?', (event_id,))
            place = cursor.fetchone()
            if not place:
                place = 1
            else:
                place = place['MAX(PLACE)'] + 1

            # Add ticket to created_tickets list
            created_tickets.append({
                'ticket_id': str(uuid.uuid4()),
                'purchase_id': purchase_id,
                'event_id': event_id,
                'purchase_date': date,
                'used': 0,
                'place': place
            })

            # Insert ticket into database
            cursor.execute('INSERT INTO TICKET (TICKET_ID, PURCHASE_ID, EVENT_ID, PURCHASE_DATE, USED, PLACE) '
                           'VALUES (?, ?, ?, ?, ?, ?)',
                           (created_tickets[-1]['ticket_id'], purchase_id, event_id, date, 0, place))

            ### Insert new vouchers

            # Get products info
            cursor.execute('SELECT PRODUCT_ID FROM CAFETERIA_PRODUCT WHERE NAME LIKE ? OR ?', ("Coffee", "Popcorn"))
            products = cursor.fetchall()
            if not products:
                return jsonify({'error': 'Products not found'}), 404

            products = [dict(product) for product in products]

            # select a random product id
            product_id = random.choice(products)['PRODUCT_ID']

            # Add voucher to created_vouchers list
            created_vouchers.append({
                'voucher_id': str(uuid.uuid4()),
                'customer_id': customer_id,
                'product_id': product_id,
                'type': 'Free Product',
                'description': 'Free {} for buying a ticket'.format(
                    products.filter(lambda x: x['PRODUCT_ID'] == product_id)[0]['NAME']),
                'redeemed': 0
            })

            # Insert voucher into database
            cursor.execute('INSERT INTO VOUCHER (VOUCHER_ID, CUSTOMER_ID, PRODUCT_ID, TYPE, DESCRIPTION, REDEEMED) '
                           'VALUES (?, ?, ?, ?, ?, ?)',
                           (created_vouchers[-1]['voucher_id'], customer_id, product_id, 'Free Product',
                            created_vouchers[-1]['description'], 0))

        # Calculate number of new vouchers to emit
        threshold = 200
        new_vouchers = int((past_purchases + total_price) / threshold) - int(past_purchases / threshold)

        # Emit new voucher for every new multiple of 200 surpassed by the customer
        for i in range(new_vouchers):
            # Add voucher to created_vouchers list
            created_vouchers.append({
                'voucher_id': str(uuid.uuid4()),
                'customer_id': customer_id,
                'product_id': None,
                'type': 'Discount',
                'description': 'Discount of 5% for the next purchase',
                'redeemed': 0
            })

            # Insert voucher into database
            cursor.execute('INSERT INTO VOUCHER (VOUCHER_ID, CUSTOMER_ID, PRODUCT_ID, TYPE, DESCRIPTION, REDEEMED) '
                           'VALUES (?, ?, ?, ?, ?, ?)',
                           (created_vouchers[-1]['voucher_id'], customer_id, None, 'Discount',
                            created_vouchers[-1]['description'], 0))

        conn.commit()

        return jsonify({
            'message': 'Tickets bought successfully',
            'purchase_id': purchase_id,
            'tickets': created_tickets,
            'vouchers': created_vouchers
        }), 201
    except Exception as e:
        return jsonify({'error': 'Error buying ticket: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()


# Initialize database
init_db()

if __name__ == '__main__':
    app.run(debug=True)
