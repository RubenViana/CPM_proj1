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


# Register and Update User route
@app.route('/register_user', methods=['PUT'])
def register_user():
    # Get data from request
    data = request.json
    name = data.get('name')
    username = data.get('username')
    password = data.get('password')
    tax_number = data.get('tax_number')
    public_key = data.get('public_key')

    # Check if all required fields are present
    if not name or not username or not password or not tax_number or not public_key:
        return jsonify({'error': 'Missing required fields'}), 400

    # Check if customer already exists
    conn, cursor = get_db()
    cursor.execute('SELECT * FROM customer WHERE username = ?', (username,))
    customer = cursor.fetchone()
    if customer:
        # Update customer info
        cursor.execute('UPDATE customer SET name = ?, password = ?, tax_number = ?, public_key = ? WHERE username = ?',
                       (name, password, tax_number, public_key, username))
        conn.commit()
        conn.close()
        return jsonify({'message': 'Customer updated successfully'}), 200

    # Insert new customer
    cursor.execute('INSERT INTO customer (name, username, password, tax_number, public_key) VALUES (?, ?, ?, ?, ?)',
                   (name, username, password, tax_number, public_key))
    conn.commit()
    conn.close()

    return jsonify({'message': 'Customer registered successfully'}), 201

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


# Initialize database
init_db()

if __name__ == '__main__':
    app.run(debug=True)
