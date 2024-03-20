import json
import random

from flask import Flask, jsonify, request
import uuid
import sqlite3
from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA256
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
    """
    Register a new user.

    Request Body Parameters:
    JSON Object with the following fields:
        - name (str): Name of the user.
        - username (str): Username of the user.
        - password (str): Password of the user.
        - tax_number (str): Tax number of the user.
        - public_key (str): Public key of the user.
        - credit_card_number (str): Credit card number of the user.
        - credit_card_validity (str): Validity of the credit card.
        - credit_card_type (str): Type of the credit card.

    Example:
        {
            "name": "John Doe",
            "username": "johndoe",
            "password": "password123",
            "tax_number": "123456789",
            "public_key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl+qMjDMeiXf09EDJq4zH\n8j7d0jQHx",
            "credit_card_number": "1234567890123456",
            "credit_card_validity": "MM/YY",
            "credit_card_type": "Visa"
        }

    Returns:
    - JSON: A message confirming successful registration along with customer_id.
    """

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
            return jsonify({'message': f'Customer {username} already exists'}), 409

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
    """
    Login route for registered users.

    Request Body Parameters:
    JSON Object with the following fields:
        - username (str): Username of the user.
        - password (str): Password of the user.

    Example:
        {
            "username": "johndoe",
            "password": "password123"
        }

    Returns:
    - JSON: A message confirming successful login.
    """

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
    """
    Get the next events.

    Request Arguments:
        - nr_of_events (int): Number of events to retrieve.

    Example:
        - /next_events?nr_of_events=5


    Returns:
    - JSON: List of next events with details.
    """
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
    """
    Get details of a specific event.

    Request Arguments:
        - event_id (str): Identifier of the event.

    Example:
        - /next_events?event_id=abcdefg

    Returns:
    - JSON: Details of the event.
    """
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
            return jsonify({'message': f'Event {event_id} not found'}), 404

        return jsonify(dict(event)), 200
    except Exception as e:
        return jsonify({'error': 'Error getting event: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()


# Buy Ticket Route
@app.route('/buy_ticket', methods=['POST'])
def buy_ticket():
    """
    Purchase tickets for an event.

    Request Parameters:
    JSON Object with the following fields:
    - customer_id (str): Identifier of the customer.
    - event_id (str): Identifier of the event.
    - nr_of_tickets (int): Number of tickets to purchase.
    - signature (str): Signature of the request.

    Example:
        {
            "customer_id": "customer_id_here",
            "event_id": "event_id_here",
            "nr_of_tickets": 2,
            "signature": "signature_here"
        }

    Returns:
    - JSON: A message confirming successful ticket purchase along with details.
    """
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
            return jsonify({'error': f'Customer {customer_id} not found'}), 404

        # Create public key from string
        public_key = RSA.importKey(customer['PUBLIC_KEY'])

        # Create a json object with data
        data = {
            'customer_id': customer_id,
            'event_id': event_id,
            'nr_of_tickets': nr_of_tickets
        }

        if not validate_message(data, public_key, signature):
            return jsonify({'error': 'Invalid signature'}), 401

        # Get ticket details
        cursor.execute('SELECT * FROM EVENT WHERE EVENT_ID = ?', (event_id,))
        event = cursor.fetchone()
        if not event:
            return jsonify({'error': f'Event {event_id} not found'}), 404

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

            # Insert ticket into the database
            cursor.execute('INSERT INTO TICKET (TICKET_ID, PURCHASE_ID, EVENT_ID, PURCHASE_DATE, USED, PLACE) '
                           'VALUES (?, ?, ?, ?, ?, ?)',
                           (created_tickets[-1]['ticket_id'], purchase_id, event_id, date, 0, place))

            ### Insert new vouchers

            # Get products info
            cursor.execute('SELECT PRODUCT_ID FROM PRODUCT WHERE NAME LIKE ? OR ?', ("Coffee", "Popcorn"))
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

        # Calculate the number of new vouchers to emit
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


@app.route('/validate_tickets', methods=['POST'])
def validate_tickets():
    """
    Validate tickets for entry to an event.

    Request Parameters:
    JSON Object with the following fields:
    - customer_id (str): Identifier of the customer.
    - tickets (list): List of tickets to validate, each ticket containing ticket_id.
    - signature (str): Signature of the request.

    Example:
        {
            "customer_id": "customer_id",
            "tickets": [
                {"ticket_id": "ticket_id_1"},
                {"ticket_id": "ticket_id_2"}
            ],
            "signature": "signature_here"
        }

    Returns:
    - JSON: 
        - A message confirming successful ticket validation.
        - If validation was successful, tickets with a validation flag and error description if not valid
    """
    try:
        # Get data from request
        data = request.json
        customer_id = data.get('customer_id')
        tickets = data.get('tickets')
        signature = data.get('signature')

        # Check if all required fields are present
        if not customer_id or not tickets or not signature:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get public key of customer
        conn, cursor = get_db()
        cursor.execute('SELECT PUBLIC_KEY FROM customer WHERE CUSTOMER_ID = ?', (customer_id,))
        customer = cursor.fetchone()

        # if customer not found
        if not customer:
            return jsonify({'error': f'Customer {customer_id} not found'}), 404

        # Create public key from string
        public_key = RSA.importKey(customer['PUBLIC_KEY'])

        # Create a json object with data
        data = {
            'customer_id': customer_id,
            'tickets': tickets,
        }

        if not validate_message(data, public_key, signature):
            return jsonify({'error': 'Invalid signature'}), 401

        # Validate tickets
        for t in tickets:
            cursor.execute('SELECT * FROM TICKET WHERE TICKET_ID = ?', (t['ticket_id'],))
            ticket = cursor.fetchone()
            # Check if ticket exists
            if not ticket:
                return jsonify({'error': f'Ticket {t['ticket_id']} not found'}), 404
            # Check if ticket is used
            if ticket['USED']:
                return jsonify({'error': f'Ticket {t['ticket_id']} already used'}), 400
            # Check if ticket purchase belongs to customer
            cursor.execute('SELECT CUSTOMER_ID FROM PURCHASE WHERE PURCHASE_ID = ?', (ticket['PURCHASE_ID']))
            c_id = cursor.fetchone().get('CUSTOMER_ID')
            if c_id != customer_id:
                return jsonify({'error': f'Ticket {t['ticket_id']} does not belong to customer'}), 400
            t['valid'] = True
            cursor.execute('UPDATE TICKET SET USED = 1 WHERE TICKET_ID = ?', (t['ticket_id'],))

        conn.commit()

        return jsonify({
            'message': 'Tickets validated successfully',
            'tickets' : tickets
        }), 200
    except Exception as e:
        return jsonify({'error': 'Error validating tickets: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Get Products Route
@app.route('/products', methods=['GET'])
def products():
    """
    Get all products.

    Request parameters:
        None

    Example:
        - /products

    :return: JSON with all products.
    """
    try:
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM PRODUCT')
        products = cursor.fetchall()
        if not products:
            return jsonify({'message': 'No products found'}), 404
        return jsonify([dict(product) for product in products]), 200
    except Exception as e:
        return jsonify({'error': 'Error getting products: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Get vouchers Route
@app.route('/vouchers', methods=['GET'])
def vouchers():
    """
    Get vouchers for a customer.
    Request Parameters:
        - customer_id (str): Identifier of the customer.

    Example:
        - /vouchers?customer_id=abcdefg

    :return: JSON with all vouchers for the customer.
    """
    try:
        args = request.args
        customer_id = args.get('customer_id')

        # if no customer_id parameter is passed
        if not customer_id:
            return jsonify({'error': 'Missing required fields'}), 400

        conn, cursor = get_db()
        cursor.execute('SELECT * FROM VOUCHER WHERE CUSTOMER_ID = ?', (customer_id,))
        vouchers = cursor.fetchall()
        if not vouchers:
            return jsonify({'message': 'No vouchers found'}), 404
        return jsonify([dict(voucher) for voucher in vouchers]), 200
    except Exception as e:
        return jsonify({'error': 'Error getting vouchers: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Get purchases Route
@app.route('/purchases', methods=['GET'])
def purchases():
    """
    Get purchases for a customer.
    Request Parameters:
        - customer_id (str): Identifier of the customer.

    Example:
        - /purchases?customer_id=abcdefg

    :return: JSON with all purchases for the customer.
    """
    try:
        args = request.args
        customer_id = args.get('customer_id')

        # if no customer_id parameter is passed
        if not customer_id:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get customer purchases
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM PURCHASE WHERE CUSTOMER_ID = ?', (customer_id,))
        purchases = cursor.fetchall()

        if not purchases:
            return jsonify({'message': 'No purchases found'}), 404

        # Convert purchases to dictionary
        purchases = [dict(purchase) for purchase in purchases]

        # Get tickets associated with the purchases
        for p in purchases:
            cursor.execute('SELECT * FROM TICKET WHERE PURCHASE_ID = ?', (p['PURCHASE_ID'],))
            tickets = cursor.fetchall()
            p['tickets'] = [dict(ticket) for ticket in tickets]

        return jsonify(purchases), 200
    except Exception as e:
        return jsonify({'error': 'Error getting purchases: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Get Purchase Receipt Route
@app.route('/purchase_receipt', methods=['GET'])
def purchase_receipt():
    """
    Get the receipt of a purchase.

    Request Parameters:
        - purchase_id (str): Identifier of the purchase.

    Example:
        - /purchase_receipt?purchase_id=abcdefg

    :return: JSON with the receipt of the purchase.
    """
    try:
        args = request.args
        purchase_id = args.get('purchase_id')

        # if no purchase_id parameter is passed
        if not purchase_id:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get purchase receipt
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM PURCHASE WHERE PURCHASE_ID = ?', (purchase_id,))
        purchase = cursor.fetchone()

        if not purchase:
            return jsonify({'message': 'Purchase not found'}), 404

        # Convert purchase to dictionary
        purchase = dict(purchase)

        # Get tickets associated with the purchase
        cursor.execute('SELECT * FROM TICKET WHERE PURCHASE_ID = ?', (purchase_id,))
        tickets = cursor.fetchall()
        purchase['tickets'] = [dict(ticket) for ticket in tickets]

        return jsonify(purchase), 200
    except Exception as e:
        return jsonify({'error': 'Error getting purchase receipt: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Get order Route
@app.route('/orders', methods=['GET'])
def orders():
    """
    Get orders for a customer.

    Request Parameters:
        - customer_id (str): Identifier of the customer.

    Example:
        - /orders?customer_id=abcdefg
    :return: JSON with all orders for the customer.
    """
    try:
        args = request.args
        customer_id = args.get('customer_id')

        # if no customer_id parameter is passed
        if not customer_id:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get customer orders
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM "ORDER" WHERE CUSTOMER_ID = ?', (customer_id,))
        orders = cursor.fetchall()

        if not orders:
            return jsonify({'message': 'No orders found'}), 404

        # Convert orders to dictionary
        orders = [dict(order) for order in orders]

        # Get products associated with the orders
        for o in orders:
            cursor.execute('SELECT PRODUCT_ID, QUANTITY FROM ORDER_PRODUCT WHERE ORDER_ID = ?', (o['ORDER_ID'],))
            products = cursor.fetchall()

            if not products:
                raise Exception(f'Products for order {o['ORDER_ID']} not found')

            o['products'] = [dict(product) for product in products]

            # Get product details for each product in the order and append the details to the corresponding entry
            for p in o['products']:
                cursor.execute('SELECT NAME, DESCRIPTION, PRICE FROM PRODUCT WHERE PRODUCT_ID = ?', (p['PRODUCT_ID'],))
                product = cursor.fetchone()
                if not product:
                    p['found'] = False
                    continue
                p['found'] = True
                p['name'] = product['NAME']
                p['description'] = product['DESCRIPTION']
                p['price'] = product['PRICE']

        return jsonify(orders), 200
    except Exception as e:
        return jsonify({'error': 'Error getting orders: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Get Order Receipt Route
@app.route('/order_receipt', methods=['GET'])
def order_receipt():
    """
    Get the receipt of an order.

    Request Parameters:
        - order_id (str): Identifier of the order.

    Example:
        - /order_receipt?order_id=abcdefg

    :return: JSON with the receipt of the order.
    """
    try:
        args = request.args
        order_id = args.get('order_id')

        # if no order_id parameter is passed
        if not order_id:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get order receipt
        conn, cursor = get_db()
        cursor.execute('SELECT * FROM "ORDER" WHERE ORDER_ID = ?', (order_id,))
        order = cursor.fetchone()

        if not order:
            return jsonify({'message': 'Order not found'}), 404

        # Convert order to dictionary
        order = dict(order)

        # Get products associated with the order
        cursor.execute('SELECT PRODUCT_ID, QUANTITY FROM ORDER_PRODUCT WHERE ORDER_ID = ?', (order_id,))
        products = cursor.fetchall()

        if not products:
            raise Exception(f'Products for order {order_id} not found')

        order['products'] = [dict(product) for product in products]

        # Get product details for each product in the order and append the details to the corresponding entry
        for p in order['products']:
            cursor.execute('SELECT NAME, DESCRIPTION, PRICE FROM PRODUCT WHERE PRODUCT_ID = ?', (p['PRODUCT_ID'],))
            product = cursor.fetchone()
            if not product:
                p['found'] = False
                continue
            p['found'] = True
            p['name'] = product['NAME']
            p['description'] = product['DESCRIPTION']
            p['price'] = product['PRICE']

        # Get vouchers associated with the order
        cursor.execute('SELECT VOUCHER_ID, PRODUCT_ID, TYPE, DESCRIPTION, REDEEMED FROM VOUCHER WHERE ORDER_ID = ?', (order_id,))
        vouchers = cursor.fetchall()

        order['vouchers'] = [dict(voucher) for voucher in vouchers]

        return jsonify(order), 200
    except Exception as e:
        return jsonify({'error': 'Error getting order receipt: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

# Validate Order Route
@app.route('/validate_order', methods=['POST'])
def validate_order():
    """
    Validate an order.

    Request Parameters:
    JSON Object with the following fields:
    - customer_id (str): Identifier of the customer.
    - products (list): List of products to validate each product containing product_id and quantity.
    - vouchers (list): List of vouchers to validate,
    each voucher containing voucher_id and product_id associated with the voucher.
    - signature (str): Signature of the request.

    Example:
        {
            "customer_id": "customer_id",
            "products": [
                {"product_id": "product_id_1", "quantity": 2},
                {"product_id": "product_id_2", "quantity": 1}
            ],
            "vouchers": [
                {"voucher_id": "voucher_id_1", "product_id": "product_id_1"},
                {"voucher_id": "voucher_id_2", "product_id": "product_id_2"}
            ],
            "signature": "signature_here"
        }
    
    Returns:
    - JSON:
        - A message confirming successful order validation.
        - If validation was successful, the total price of the order, the order_id, products and vouchers with a validation flag and error description if not valid

    """
    try:
        # Get data from request
        data = request.json
        customer_id = data.get('customer_id')
        products = data.get('products')
        vouchers = data.get('vouchers')
        signature = data.get('signature')

        # Check if all required fields are present
        if not customer_id or not products or not vouchers or not signature:
            return jsonify({'error': 'Missing required fields'}), 400
        
        # Check if there are more than 2 vouchers
        if len(vouchers) > 2:
            return jsonify({'error': 'Only two vouchers can be used per order'}), 400
        

        # Get public key of customer
        conn, cursor = get_db()
        cursor.execute('SELECT PUBLIC_KEY FROM customer WHERE CUSTOMER_ID = ?', (customer_id,))
        customer = cursor.fetchone()

        # If customer not found
        if not customer:
            return jsonify({'error': f'Customer {customer_id} not found'}), 404

        # Create public key from string
        public_key = RSA.importKey(customer['PUBLIC_KEY'])

        # Create a json object with data
        data = {
            'customer_id': customer_id,
            'products': products,
            'vouchers': vouchers
        }

        if not validate_message(data, public_key, signature):
            return jsonify({'error': 'Invalid signature'}), 401

        # Validate products
        for p in products:
            cursor.execute('SELECT * FROM PRODUCT WHERE PRODUCT_ID = ?', (p['product_id'],))
            product = cursor.fetchone()
            # Check if the product exists
            if not product:
                return jsonify({'error': f'Product {p['product_id']} not found'}), 404


        discount_vouchers = 0

        # Validate vouchers
        for v in vouchers:
            cursor.execute('SELECT * FROM VOUCHER WHERE VOUCHER_ID = ?', (v['voucher_id'],))
            voucher = cursor.fetchone()
            # Check if voucher exists
            if not voucher:
                v['accepted'] = False
                v['error'] = f'Voucher {v['voucher_id']} not found'
            # Check the voucher type
            if voucher['TYPE'] == 'Discount':
                discount_vouchers += 1
            if discount_vouchers > 1:
                v['accepted'] = False
                v['error'] = 'Only one discount voucher can be used per order'
            # Check if voucher belongs to customer
            if voucher['CUSTOMER_ID'] != customer_id:
                v['accepted'] = False
                v['error'] = f'Voucher {v['voucher_id']} does not belong to customer'
            # Check if voucher is redeemed
            if voucher['REDEEMED']:
                v['accepted'] = False
                v['error'] = f'Voucher {v['voucher_id']} already redeemed'
            # Check if the voucher is for the right product
            if voucher['PRODUCT_ID'] != v['product_id'] and voucher['TYPE'] == 'Free Product':
                v['accepted'] = False
                v['error'] = f'Voucher {v['voucher_id']} is not for product {v['product_id']}'
            v['accepted'] = True
            v['applied_to_order'] = False

        conn.commit()

        # Calculate the total price of the order
        total_price = 0
        # Duplicate the products list
        prods = products.copy()
        # Decrease the quantity of the products to a minimum of 0 by the number of products that have a accepted 'Free Product' voucher
        for v in vouchers:
            if v['accepted'] and v['applied_to_order'] and v['type'] == 'Free Product':
                for p in prods:
                    if p['product_id'] == v['product_id']:
                        p['quantity'] -= 1
                        v['applied_to_order'] = True
                        if p['quantity'] < 0:
                            p['quantity'] = 0
                            break

        # Calculate the total price of the order
        for p in prods:
            cursor.execute('SELECT PRICE FROM PRODUCT WHERE PRODUCT_ID = ?', (p['product_id'],))
            price = cursor.fetchone()['PRICE']
            total_price += price * p['quantity']

        # Apply discount if there are discount vouchers
        if discount_vouchers>0:
            total_price *= 0.95

        # Calculate total price of past purchases for the customer
        cursor.execute('SELECT SUM(TOTAL_PRICE) FROM PURCHASE WHERE CUSTOMER_ID = ?', (customer_id,))
        past_purchases = cursor.fetchone()
        if not past_purchases:
            past_purchases = 0
        else:
            past_purchases = past_purchases['SUM(TOTAL_PRICE)']

            # Calculate the number of new vouchers to emit
        threshold = 200
        new_vouchers = int((past_purchases + total_price) / threshold) - int(past_purchases / threshold)

        # Emit new voucher for every new multiple of 200 surpassed by the customer
        for i in range(new_vouchers):
            # Insert voucher into database
            cursor.execute('INSERT INTO VOUCHER (VOUCHER_ID, CUSTOMER_ID, PRODUCT_ID, TYPE, DESCRIPTION, REDEEMED) '
                           'VALUES (?, ?, ?, ?, ?, ?)',
                           (str(uuid.uuid4()), customer_id, None, 'Discount',
                            'Discount of 5% for the next purchase', 0))

        # Insert new order into the database
        cursor.execute('INSERT INTO "ORDER" (CUSTOMER_ID, DATE, TOTAL_PRICE) VALUES (?, datetime(), ?)',
                       (customer_id, total_price))
        order_id = cursor.lastrowid

        # Insert products into the order
        for p in prods:
            cursor.execute('INSERT INTO ORDER_PRODUCT (ORDER_ID, PRODUCT_ID, QUANTITY) VALUES (?, ?, ?)',
                           (order_id, p['product_id'], p['quantity']))

        # Update vouchers to associate them with the order
        for v in vouchers:
            if v['applied_to_order']:
                cursor.execute('UPDATE VOUCHER SET ORDER_ID = ? WHERE VOUCHER_ID = ?', (order_id, v['voucher_id'],))

        conn.commit()

        return jsonify({
            # Message with the number and type of voucher applied or not
            'message': 'Order validation completed',
            'total_price': total_price,
            'order_id': order_id,
            'products': products,
            'vouchers': vouchers
        }), 200
    
    except Exception as e:
        return jsonify({'error': 'Error validating order: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()
# Pay Order Route
@app.route('/pay_order', methods=['POST'])
def pay_order():
    """
    Pay an order.

    Request Parameters:
    JSON Object with the following fields:
    - customer_id (str): Identifier of the customer.
    - order_id (str): Identifier of the order.
    - signature (str): Signature of the request.

    Example:
        {
            "customer_id": "customer_id",
            "order_id": "order_id",
            "signature": "signature_here"
        }

    Returns:
    - JSON: A message confirming successful order payment.
    """
    try:
        # Get data from request
        data = request.json
        customer_id = data.get('customer_id')
        order_id = data.get('order_id')
        signature = data.get('signature')

        # Check if all required fields are present
        if not customer_id or not order_id or not signature:
            return jsonify({'error': 'Missing required fields'}), 400

        # Get public key of customer
        conn, cursor = get_db()
        cursor.execute('SELECT PUBLIC_KEY FROM customer WHERE CUSTOMER_ID = ?', (customer_id,))
        customer = cursor.fetchone()

        # if customer not found
        if not customer:
            return jsonify({'error': f'Customer {customer_id} not found'}), 404

        # Create public key from string
        public_key = RSA.importKey(customer['PUBLIC_KEY'])

        # Create a json object with data
        data = {
            'customer_id': customer_id,
            'order_id': order_id
        }

        if not validate_message(data, public_key, signature):
            return jsonify({'error': 'Invalid signature'}), 401

        # Pay order
        cursor.execute('UPDATE "ORDER" SET PAID = 1 WHERE ORDER_ID = ?', (order_id,))

        # Set vouchers as redeemed
        cursor.execute('UPDATE VOUCHER SET REDEEMED = 1 WHERE ORDER_ID = ?', (order_id,))

        conn.commit()

        return jsonify({'message': 'Order paid successfully'}), 200
    except Exception as e:
        return jsonify({'error': 'Error paying order: {}'.format(e)}), 500
    finally:
        if conn:
            conn.close()

def validate_message(data, public_key, signature):
    message = json.dumps(data, sort_keys=True)
    hash_object = SHA256.new(message.encode())
        # select vouchers where voucher id in list of voucher ids
    verifier = PKCS1_v1_5.new(public_key)
    return verifier.verify(hash_object, base64.b64decode(signature))


# Initialize database
init_db()

if __name__ == '__main__':
    app.run(debug=True)
