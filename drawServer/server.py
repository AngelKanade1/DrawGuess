from flask import Flask, request
import json
from gevent import pywsgi
from flask_socketio import SocketIO, emit

app = Flask(__name__)
socketio = SocketIO(app)

players = []
isStart = False


@app.route('/login', methods=['POST', "GET"])
def login():
    backdata = {}

    if len(request.get_data()) != 0:
        id = request.values.get("id")
        if not isStart:
            players.append(id)
            backdata["errcode"] = 0
        else:
            backdata["errcode"] = 1

    return json.dumps(backdata, ensure_ascii=False)


@socketio.on('connect')
def test_connect():
    emit('my response', {'data': 'Connected'})


if __name__ == '__main__':
    with open("config.json", "r", encoding="utf-8") as c:
        config = json.load(c)
        h = config["host"]
        p = config["port"]
    # server = pywsgi.WSGIServer((h, int(p)), app)
    # server.serve_forever()
    app.run()
