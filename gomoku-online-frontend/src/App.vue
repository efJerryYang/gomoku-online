<template>
  <div class="app">
    <div class="home" v-if="!matchedPlayer">
      <div class="input-box">
        <label for="username">Username: </label>
        <input type="text" id="username" v-model="username">
        <button @click="pickUsername">Pick</button>
      </div>
      <!-- TODO: Better alternatives, Messages Popup -->
      <div class="message">{{ notification }}</div>
      <div class="matching-list">
        <table class="matching-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Waiting Time</th>
              <th>Username</th>
              <th><button class="dice" @click="matchWithRandomPlayer">Dice</button></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(player, index) in matchingList" :key="index">
              <td> {{ index + 1 }}</td>
              <td>{{ formatWaitingTime(player.joinTime) }}</td>
              <td>{{ player.username }}</td>
              <td>
                <button v-if="this.id !== player.id" @click="matchWithPlayer(player)">Match</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="game" v-else>
      <div class="status">
        <div class="you" :class="yourTurn ? 'bold' : ''">{{ username }}</div>
        <div class="opponent" :class="!yourTurn ? 'bold' : ''">{{ matchedPlayer.username }}</div>
        <div class="timer">{{ remainingTime }}</div>
      </div>
      <div class="board">
        <div class="row" v-for="(row, rowIndex) in board" :key="rowIndex">
          <div class="cell" v-for="(cell, cellIndex) in row" :key="cellIndex" @click="placeStone(rowIndex, cellIndex)">
            <img :src="cell" :alt="cell" />
          </div>
        </div>
        <div class="result-panel">
          <div class="result">{{ result }}</div>
          <button @click="startNewRound"> Start New Round </button>
          <button @click="exitGame"> Exit Game </button>
        </div>
      </div>
    </div>
    <!-- <div v-if="showPopup" class="popup">
      <div class="popup-content">
        <div class="popup-message">{{ popupMessage }}</div>
        <div class="popup-timer">
          <div class="popup-timer-bar" :style="{ width: timerWidth }"></div>
        </div>
        <div class="popup-buttons">
          <button @click="onConfirm(true)">Accept</button>
          <button @click="onConfirm(false)">Reject</button>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      id: null,
      matchedPlayer: null,
      notification: '',
      matchingList: [],
      yourTurn: false,
      remainingTime: 30,
      board: [], // Initialize board with empty array of 15x15
      result: 'You: 0, Opponent: 0',
      gameId: null,

      showPopup: false,
      popupMessage: '',
      popupTimer: null,
      timerWidth: '100%',
    };
  },
  mounted() {
    setInterval(() => {
      this.refreshMatchingList();
    }, 1000);
    // check matching status
    setInterval(() => {
      if (this.id) {
        this.checkMatchingStatus();
      }
    }, 1000);
  },
  methods: {
    async refreshMatchingList() {
      try {
        // const token = await this.getJwtToken();
        const response = await axios.get('/api/match');
        if (response.status === 200) {
          this.matchingList = response.data.reverse();
        }
      } catch (error) {
        console.error(error);
      }
    },
    formatWaitingTime(joinTime) {
      const currentTime = new Date().getTime() / 1000;
      const waitingTime = Math.floor((currentTime - joinTime));
      if (waitingTime < 60) {
        return `${waitingTime} s`;
      } else if (waitingTime < 3600) {
        const minutes = Math.floor(waitingTime / 60);
        const seconds = waitingTime % 60;
        return `${minutes} min ${seconds} s`;
      } else {
        const hours = Math.floor(waitingTime / 3600);
        const minutes = Math.floor((waitingTime % 3600) / 60);
        const seconds = waitingTime % 60;
        return `${hours} h ${minutes} min ${seconds} s`
      }
    },
    // checkMatchingStatus
    async checkMatchingStatus() {
      try {
        const token = await this.getJwtToken();
        const response = await axios.get('/api/matching', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          // this.handleMatchingConfirmResponse(response);
          
        }
      } catch (error) {
        console.error(error);
      }
    },
    // handleMatchingConfirmResponse(response) {
    //   if (response.matchWithPlayer && response.data.info.toLowerCase() === 'matching') {
    //     this.showPopup = true;
    //     this.popupMessage = `You are matched with ${response.data.matchedPlayer.username} (${response.data.matchedPlayer.id})`;
    //     this.popupTimer = setInterval(() => {
    //       if (this.timerWidth === '0%') {
    //         clearInterval(this.popupTimer);
    //         this.onConfirm(false);
    //       } else {
    //         console.log("logging:", this.showPopup, this.popupTimer);
    //         this.timerWidth = `${parseInt(this.timerWidth) - 5}%`;
    //       }
    //     }, 1000);
    //   }
    // },
    // onConfirm(result) {
    //   clearInterval(this.popupTimer);
    //   this.showPopup = false;
    //   this.timerWidth = '100%';
    //   // post confirm result
    //   this.$http.post('/api/matchConfirm', { result: result });
    //   // need Auth here
    // },
    async getJwtToken() {
      let token = localStorage.getItem('jwtToken');
      let clientId = Math.random().toString(36).substring(2);
      if (!token) {
        try {
          const response = await axios.post('/api/token', {
            clientId: clientId
          });
          if (response.status === 200) {
            token = response.data.token;
            localStorage.setItem('jwtToken', token);
          }
        } catch (error) {
          console.error(error);
        }
      }
      return token;
    },

    async pickUsername() {
      if (!this.username) {
        this.notification = 'Please enter a username';
        return;
      }
      if (/^\s+$/.test(this.username)) {
        this.notification = 'Username cannot be all spaces';
        return;
      }
      this.username = this.username.trim();
      console.log("logging:" + this.username);
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/pick', {
          username: this.username
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        // if no response is received, the request failed
        if (response.status === 200) {
          this.id = response.data.id;
          this.notification = 'Successfully joined the matching list: ' + this.username + ' (' + this.id + ')';
          this.matchedPlayer = null;
          this.yourTurn = false;
        }
        else {
          this.notification = 'Failed to join the matching list';
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
      console.log("logging:" + response);
    },

    async matchWithPlayer(player) {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/match', {
          userId: this.id,
          opponentId: player.id
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          this.notification = 'Successfully matched with a player';
          this.matchedPlayer = response.data;
          console.log("logging:", response.data);
          this.board = response.data.board;
          this.yourTurn = response.data.yourTurn;
          this.remainingTime = response.data.remainingTime;
          this.gameId = response.data.id;
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },
    async matchWithRandomPlayer() {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/match/dice', {
          username: this.username
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          this.notification = 'Successfully matched with a player';
          this.matchedPlayer = response.data;
          this.board = response.data.board;
          this.yourTurn = response.data.yourTurn;
          this.remainingTime = response.data.remainingTime;
          this.gameId = response.data.id;
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },

    async placeStone(row, col) {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/game', {
          id: this.gameId,
          row,
          col
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          this.board = response.data.board;
          this.yourTurn = response.data.yourTurn;
          this.remainingTime = response.data.remainingTime;
          this.result = `You: ${response.data.player1Score}, Opponent: ${response.data.player2Score}`;

          // set the class of the cell based on the player's turn                                                                                                                                                        
          const cellClass = response.data.turn === 1 ? 'white' : 'black';
          const cell = document.querySelector(`.board .row:nth-child(${row + 1}) .cell:nth-child(${col + 1})`);
          cell.classList.add(cellClass);

          // check for game over
          if (response.data.result !== null) {
            if (response.data.result === 0) {
              this.notification = "It's a tie!";
            } else if (
              (response.data.result === 1 && response.data.yourTurn) ||
              (response.data.result === 2 && !response.data.yourTurn)
            ) {
              this.notification = 'You win!';
            } else {
              this.notification = 'You lose!';
            }
          }
        }
      } catch (error) {
        console.error(error);
      }
    },
    async startNewRound() {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/game', {
          id: this.gameId,
          clear: true
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          this.board = response.data.board;
          this.yourTurn = response.data.yourTurn;
          this.remainingTime = response.data.remainingTime;
          this.result = `You: ${response.data.player1Score}, Opponent: ${response.data.player2Score}`;
          this.notification = '';
        }
      } catch (error) {
        console.error(error);
      }
    },
    async exitGame() {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/game/exit', {
          id: this.gameId
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          this.notification = 'Successfully exited the game';
          this.matchedPlayer = null;
          this.gameId = null;
          this.board = this.createEmptyBoard();
          this.result = 'You: 0, Opponent: 0';
          this.notification = '';
        }
      } catch (error) {
        console.error(error);
      }
    },
    createEmptyBoard() {
      const board = [];
      for (let i = 0; i < 15; i++) {
        board.push(new Array(15).fill('assets/background.gif'));
      }
      return board;
    },
    /* Add API calls for getting history list and history info */
  },
  computed: {
    /* Computed properties for calculating board-related data */
  },
  watch: {
    /* Watchers for data changes */
  }
};
</script>
<style>
/* CSS styles for the app */
.app {
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.home {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.input-box {
  display: flex;
  margin-bottom: 10px;
}

.input-box input[type="text"] {
  font-size: 18px;
  padding: 5px;
  margin-right: 10px;
  border: none;
  outline: none;
  border-bottom: 2px solid #ccc;
}

.input-box button {
  font-size: 18px;
  padding: 5px;
  border-radius: 3px;
  background-color: #4caf50;
  /* green */
  color: white;
  border: none;
  cursor: pointer;
}

.message {
  font-size: 18px;
  color: red;
  margin-bottom: 10px;
}

.dice {
  font-size: 18px;
  padding: 5px;
  border-radius: 3px;
  background-color: #2196f3;
  /* blue */
  color: white;
  border: none;
  cursor: pointer;
  margin-bottom: 10px;
}

.matching-list {
  overflow-y: auto;
  max-height: 300px;
}

.matching-list table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 10px;
}

.matching-list table td,
.matching-list table th {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.matching-list table th {
  background-color: #f2f2f2;
}

.matching-list table td button {
  font-size: 14px;
  padding: 5px;
  border-radius: 3px;
  background-color: #4caf50;
  /* green */
  color: white;
  border: none;
  cursor: pointer;
}

.game {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.status {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.status div {
  font-size: 24px;
  margin-right: 20px;
}

.status div.bold {
  font-weight: bold;
}

.timer {
  font-size: 30px;
  margin-left: 20px;
}

.board {
  display: grid;
  grid-template-columns: repeat(10, 50px);
  grid-template-rows: repeat(10, 50px);
  margin-bottom: 20px;
}

.board .cell {
  display: block;
  /* background-color: #000000; */
  border-left: 1px solid #000000;
  border-right: 1px solid #000000;
  border-bottom: 1px solid #000000;
  border-top: 1px solid #000000;
  /* width: 50px; */
  /* height: 50px; */
}

.board .cell img {
  max-width: 100%;
}

.board .cell.white {
  background-image: url('src/assets/whiteStone.gif');
}

.board .cell.black {
  background-image: url('src/assets/blackStone.gif');
}

.result-panel {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.result {
  font-size: 24px;
  margin-bottom: 20px;
}

.result-panel button {
  font-size: 18px;
  padding: 5px;
  border-radius: 3px;
  background-color: #4caf50;
  /* green */
  color: white;
  border: none;
  cursor: pointer;
  margin-right: 20px;
}

.result-panel button:last-child {
  background-color: #f44336;
  /* red */
}

.popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.popup-content {
  width: 300px;
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.popup-message {
  font-size: 18px;
  margin-bottom: 20px;
}

.popup-timer {
  width: 100%;
  height: 10px;
  background-color: lightgray;
  border-radius: 5px;
  margin-bottom: 20px;
}

.popup-timer-bar {
  height: 100%;
  background-color: green;
  border-radius: 5px;
}

.popup-buttons {
  display: flex;
  justify-content: space-around;
  width: 100%;
}
</style>