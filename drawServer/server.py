from flask import Flask, request
import json
from gevent import pywsgi
from flask_socketio import SocketIO, emit
import numpy as np
import cv2
import random

app = Flask(__name__)
socketio = SocketIO(app)

words = []
players = []
unfinishPlayers = []
playersInfo = {}
isStart = False
gameRound = 0


@socketio.on('connect', namespace='/test')
def test_connect():
    global isStart
    if not isStart:
        emit('my response', {'data': 'Connected'})


@socketio.on('loginbind', namespace='/test')
def handle_id(id):
    global isStart, playersInfo
    if not isStart:
        playersInfo[id] = {"sid": request.sid}


@app.route('/login', methods=['POST'])
def login():
    global isStart, players
    if not isStart:
        id = request.values.get("id")
        players.append(id)
        return "true"
    return "false"


@app.route('/bitmap', methods=['POST'])
def bitmap():
    global players, gameRound, unfinishPlayers
    id = request.values.get("id")
    bitmap = request.values.get("bitmap")
    players[id][str(gameRound)] = {"bitmap": bitmap}
    print(id+"已上传"+str(gameRound)+"轮的bitmap")
    unfinishPlayers.remove(id)
    if unfinishPlayers == []:
        unfinishPlayers = players
        emit('message', {'data': 'startguess'})


@app.route('/guess', methods=['POST'])
def guess():
    global players, gameRound, unfinishPlayers
    id = request.values.get("id")
    guess = request.values.get("guess")
    players[id][str(gameRound)] = {"guess": guess}
    print(id+"已上传"+str(gameRound)+"轮的guess")
    unfinishPlayers.remove(id)
    if unfinishPlayers == []:
        if gameRound != int(len(players)/2):
            emit('message', {'data': 'startdraw'})
        else:
            emit('message', {'data': 'endgame'})
            unfinishPlayers = players


@app.route('/getquestion', methods=['POST'])
def getQuestion():
    global players, playersInfo, gameRound, words
    id = request.values.get("id")
    if gameRound == 1:
        word = random.choice(words)
        playersInfo[id]["question"] = word
        words.remove(word)
        return word
    if players.index(id) == 0:
        return playersInfo[players[-1]][str(gameRound-1)+"guess"]
    return playersInfo[players[players.index(id)-1]][str(gameRound-1)+"guess"]


@app.route('/getbitmap', methods=['POST'])
def getbitmap():
    global players, gameRound, playersInfo
    id = request.values.get("id")
    if players.index(id) == 0:
        return playersInfo[players[-1]][str(gameRound)+"bitmap"]
    return playersInfo[players[players.index(id)-1]][str(gameRound)+"bitmap"]


@app.route('/players', methods=['POST'])
def players():
    global players
    return ','.join(str(x) for x in players)


@app.route('/rest', methods=['POST'])
def rest():
    global unfinishPlayers
    return ','.join(str(x) for x in unfinishPlayers)


@app.route('/start')
def start():
    print(1)
    global isStart, unfinishPlayers, words, gameRound
    isStart = True
    gameRound += 1
    unfinishPlayers = players
    words = []
    emit('message', {'data': 'gamestart'})


@app.route('/getinfo', methods=['POST'])
def getinfo():
    global playersInfo
    playersInfo["index"] = ",".join(str(x) for x in players)
    return str(playersInfo)


@app.route('/checktimes', methods=['POST'])
def checktimes():
    global unfinishPlayers, players
    id = request.values.get("id")
    unfinishPlayers.remove(id)
    if unfinishPlayers == []:
        emit('message', {'data': 'endcheck'})
        unfinishPlayers = players


@app.route('/again')
def again():
    print(2)
    global unfinishPlayers, players, gameRound, playersInfo, isStart, words
    unfinishPlayers = []
    players = []
    gameRound = 0
    playersInfo = {}
    isStart = False
    words = []


if __name__ == '__main__':
    # with open("config.json", "r", encoding="utf-8") as c:
    #     config = json.load(c)
    #     h = config["host"]
    #     p = config["port"]
    # server = pywsgi.WSGIServer((h, int(p)), app)
    # server.serve_forever()
    # socketio.run(app, host='0.0.0.0', port=5000, debug=True)
    app.run(host="0.0.0.0", port=5000)
