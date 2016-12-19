import socketio
import eventlet
import time, random
import eventlet.wsgi
from flask import Flask, render_template

eventlet.monkey_patch()

sio = socketio.Server()
app = Flask(__name__)

@app.route('/')
def index():
    """Serve the client-side application."""
    return render_template('index.html')

@sio.on('connect')
def connect(sid, environ):
    print("connect", sid)

@sio.on('test node')
def testtopic(sid, data):
    print(sid, data)
    sio.emit('node', data={"y": random.random(), "x": int(time.time()) * 1000})

@sio.on('disconnect')
def disconnect(sid):
    print('disconnect ', sid)


def spammer():
    """ sends messages in a background """
    while True:
        time.sleep(1)
        sio.emit('timeseries topic', data={"y": random.random(), "x": int(time.time()) * 1000})

def stacked_spammer():
    """ sends message for stacked chart """
    while True:
        time.sleep(1)
        data = [{
            "name": 'param1',
            "data": [int(random.random()*10) for _ in range(5)]
        }, {
            "name": 'param2',
            "data": [int(random.random()*10) for _ in range(5)]
        }, {
            "name": 'param3',
            "data": [int(random.random()*10) for _ in range(5)]
        }]
        sio.emit('stacked', data=data)

def data_spammer():
    """ sends some random data """
    while True:
        time.sleep(1)
        data = {
            "something": int(random.random() * 10),
            "else": int(random.random() * 100)
        }
        sio.emit('data topic', data=data)

eventlet.spawn(spammer)
eventlet.spawn(stacked_spammer)
eventlet.spawn(data_spammer)

if __name__ == '__main__':
    # wrap Flask application with engineio's middleware
    app = socketio.Middleware(sio, app)

    # deploy as an eventlet WSGI server
    eventlet.wsgi.server(eventlet.listen(('', 8000)), app)
