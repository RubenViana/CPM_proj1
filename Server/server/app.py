import json

from flask import Flask, jsonify, request
import uuid
import sqlite3

# Initialize Flask app
app = Flask(__name__)

DB_FILE = 'database/server.db'
DB_SCHEMA = 'database/schema.sql'


# Initialize database
def init_db():
    print('Initializing database')
    conn = sqlite3.connect(DB_FILE)
    with open(DB_SCHEMA) as f:
        conn.executescript(f.read())
    print('Database initialized')
    conn.close()


def get_db():
    conn = sqlite3.connect(DB_FILE)
    conn.row_factory = sqlite3.Row
    return conn, conn.cursor()


# Register User route
@app.route('/register_user', methods=['POST'])
def register_user():
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
    if not name or not username or not password or not tax_number or not public_key:
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
    cursor.execute('INSERT INTO customer (CUSTOMER_ID, USERNAME, PASSWORD, TAX_NUMBER, PUBLIC_KEY, NAME) VALUES (?, ?, ?, ?, ?, ?)',
                   (customer_id, username, password, tax_number, public_key, name))

    # Insert new credit card
    cursor.execute('INSERT INTO credit_card (CUSTOMER_ID, TYPE, NUMBER, VALIDITY) VALUES (?, ?, ?, ?)',
                   (customer_id, credit_card_type, credit_card_number, credit_card_validity))
    conn.commit()
    conn.close()

    return jsonify({
                        'message': 'Customer registered successfully',
                        'customer_id': customer_id
                    }), 201

# Login Route
@app.route('/login', methods=['POST'])
def login():
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
    conn.close()

    if not customer:
        return jsonify({'error': 'Invalid username or password'}), 401

    return jsonify({'message': 'Login successful'}), 200

# Next Shows Route
@app.route('/next_shows', methods=['GET'])
def next_shows():
    args = request.args

    nr_of_shows = args['shows']

    # Query next nr_of_shows
    conn, cursor = get_db()
    cursor.execute('SELECT * FROM EVENT as e where e.DATE >= date() limit ?', nr_of_shows)
    shows = cursor.fetchall()
    conn.close()

    # If no shows are found
    if not shows:
        return jsonify({'message': 'No shows found'}), 404

    return jsonify([dict(show) for show in shows]), 200



# Initialize database
init_db()

if __name__ == '__main__':
    app.run(debug=True)
