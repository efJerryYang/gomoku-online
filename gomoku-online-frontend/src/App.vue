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
      <div class="board">
        <div class="result-panel">
          <div class="score-panel">{{ historyScore }}</div>
        </div>
        <div class="status">
          <div class="you" :class="yourTurn ? 'bold' : ''">You: {{ username }}</div>
          <div class="opponent" :class="!yourTurn ? 'bold' : ''">Opponent: {{ matchedPlayer.username }}</div>
          <div class="timer">Timer: {{ remainingTime }}</div>
        </div>
        <div class="row" v-for="(row, rowIndex) in board" :key="rowIndex">
          <div class="cell" v-for="(cell, cellIndex) in row" :key="cellIndex" @click="placeStone(rowIndex, cellIndex)">
            <img :src="cell" :alt="cell" />
          </div>
        </div>
        <div class="result-panel">
          <div class="message">{{ warningMessage }}</div>
          <div class="result">{{ notification }}</div>
          <div class="buttons">
            <button @click="startNewRound"> Start New Round </button>
            <button @click="exitGame"> Exit Game </button>
          </div>
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
import background from '@/assets/background.gif';
import blackStone from '@/assets/blackStone.gif';
import whiteStone from '@/assets/whiteStone.gif';

export default {
  data() {
    return {
      username: '',
      id: null,
      matchedPlayer: null,
      notification: '',
      matchingList: [],
      yourTurn: false,
      remainingTime: 60,
      board: [], // Initialize board with empty array of 10x10
      result: 0,
      historyScore: 'You: 0, Opponent: 0',
      gameId: null,
      yourStone: null,
      warningMessage: null,

      showPopup: false,
      popupMessage: '',
      popupTimer: null,
      timerWidth: '100%',
    };
  },
  mounted() {
    setInterval(() => {
      if (!this.matchedPlayer) {
        this.refreshMatchingList();
        if (this.id) {
          // check matching status
          this.checkMatchingStatus();
        }
      } else {
        // if (!this.yourTurn) // opponent's turn
        // console.log('logging (yourTurn):', this.yourTurn);
        this.checkGameStatus();
        // else // your turn
        if (this.result < 2) {
          if (this.remainingTime > 0) {
            this.remainingTime--;
          }
        }
      }
      console.log('logging (matchedPlayer):', this.matchedPlayer);
    }, 1000);
  },
  methods: {
    init() {
      this.username = '';
      this.id = null;
      this.matchedPlayer = null;
      this.notification = '';
      this.matchingList = [];
      this.yourTurn = false;
      this.remainingTime = 60;
      this.board = [];
      this.result = 0;
      this.historyScore = 'You: 0, Opponent: 0';
      this.gameId = null;
      this.yourStone = null;
      this.warningMessage = null;
    },
    processGameResponse(game) {
      this.gameId = game.id;
      let isThisYourTurn = game.whoseTurn === this.id;
      if (isThisYourTurn !== this.yourTurn) {
        this.remainingTime = 60;
      }
      this.yourTurn = isThisYourTurn;
      this.board = this.fillBoard(game.board);
      this.notification = this.yourTurn ? 'This is your turn' : 'This is opponent\'s turn';
      let you;
      let opponent;
      if (game.player1.id === this.id) {
        you = game.player1;
        this.yourStone = game.player1Stone === 1 ? blackStone : whiteStone;
        opponent = game.player2;
      } else {
        you = game.player2;
        this.yourStone = game.player2Stone === 1 ? blackStone : whiteStone;
        opponent = game.player1;
      }
      this.historyScore = `You: ${you.score}, Opponent: ${opponent.score}`;
      this.matchedPlayer = opponent;
      this.result = game.result;
    },
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
    fillBoard(responseBoard) {
      // 0 -> background.gif
      // 1 -> blackStone.gif
      // 2 -> whiteStone.gif
      const board = [];
      const boardSize = responseBoard.length;
      for (let i = 0; i < boardSize; i++) {
        const row = [];
        for (let j = 0; j < boardSize; j++) {
          if (responseBoard[i][j] === 0) {
            row.push(background);
          } else if (responseBoard[i][j] === 1) {
            row.push(blackStone);
          } else if (responseBoard[i][j] === 2) {
            row.push(whiteStone);
          } else {
            row.push(null);
          }
        }
        board.push(row);
      }
      return board;
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
          let game = response.data;
          console.log('logging (checkMatchingStatus):', game);
          if (game.id && game.player1 && game.player2) {
            this.processGameResponse(game);
          }
        }
      } catch (error) {
        console.error(error);
      }
    },
    async checkGameStatus() {
      // Get GameDTO
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/game', {
          gameId: this.gameId
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          let game = response.data;
          console.log('logging (checkGameStatus):', game);
          if (game.id && game.player1 && game.player2) {
            this.processGameResponse(game);
            //     // Game info
            // public static final int GAME_STATUS_PENDING = 0;
            // public static final int GAME_STATUS_PLAYING = 1;
            // public static final int GAME_STATUS_IT_IS_A_TIE = 2;
            // public static final int GAME_STATUS_PLAYER1_WIN = 3;
            // public static final int GAME_STATUS_PLAYER2_WIN = 4;
            // public static final int GAME_STATUS = 5;
            if (this.result > 1) {
              if (this.result === 2) {
                this.notification = 'It is a tie';
              } else if (this.result === 3 && this.id === game.player1.id) {
                this.notification = 'You won!';
              } else if (this.result === 4 && this.id === game.player2.id) {
                this.notification = 'You won!';
              } else if (this.result === 3 && this.id === game.player2.id) {
                this.notification = 'You lost...';
              } else if (this.result === 4 && this.id === game.player1.id) {
                this.notification = 'You lost...';
              } else if (this.result === 5) {
                this.init()
              }
            }
          } else {
            // current game become invalid
            this.init();
          }
        }
      } catch (error) {
        console.error(error);
      }
      // For user who submit the move should be just the move and timestamp, but whether another user should only update the move it is not clear
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
      console.log("logging (pickUsername):", this.username);
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
          this.matchedPlayer = null;
          this.id = response.data.id;
          this.notification = 'Successfully joined the matching list: ' + this.username + ' (' + this.id + ')';
          this.yourTurn = false;
        }
        else {
          this.notification = 'Failed to join the matching list';
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
      console.log("logging (pickUsername response):", response);
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
          let game = response.data;
          console.log('logging (matchWithPlayer):', game);
          if (game.id && game.player1 && game.player2) {
            this.processGameResponse(game);
          }
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },
    async matchWithRandomPlayer() {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/match/dice', {
          userId: this.id
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          let game = response.data;
          console.log('logging (matchWithRandomPlayer):', game);
          if (game.id && game.player1 && game.player2) {
            this.processGameResponse(game);
          }
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },

    async placeStone(row, col) {
      this.warningMessage = null;
      if (this.board[row][col] === background && this.yourTurn && this.result === 1) {
        this.board[row][col] = this.yourStone;
        console.log("logging (placeStone): yourTurn:", this.yourTurn);
        this.remainingTime = 60;
      }
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/move', {
          gameId: this.gameId,
          x: row,
          y: col,
          moveTime: Date.now() // timestamp_ms
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        // place a stone directly to better user experience
        // should do validation of the stone position
        // only position that is empty can be placed
        if (response.status === 200) {
          let game = response.data;
          console.log('logging (placeStone):', game);
          if (game.id && game.player1 && game.player2) {
            this.processGameResponse(game);
          }
        }
      } catch (error) {
        console.error(error);
      }
    },
    async startNewRound() {
      console.log("logging (startNewRound):", this.result, this.gameId);
      if (this.result === 0 || this.result === 1) {
        this.warningMessage = 'You cannot start a new round when playing!';
        return;
      }
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/game/new', {
          gameId: this.gameId
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          let game = response.data;
          console.log('logging (startNewRound):', game);
          if (game.id && game.player1 && game.player2) {
            this.processGameResponse(game);
          }
        }
      } catch (error) {
        console.error(error);
      }
    },
    async exitGame() {
      try {
        const token = await this.getJwtToken();
        const response = await axios.post('/api/game/exit', {
          gameId: this.gameId
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        if (response.status === 200) {
          let game = response.data;
          console.log('logging (exitGame):', game);
          this.init();
        }
      } catch (error) {
        console.error(error);
      }
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
  margin-top: 20px;
}

.status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 10px;
}

.status div {
  font-size: 20px;
  margin-right: 20px;
}

.status div.bold {
  font-weight: bold;
}

.you,
.opponent {
  font-weight: normal;
}

.bold {
  font-weight: bold;
}

.board {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.board .row {
  display: table-row;
  font-size: 0;
}

.board .row .cell {
  display: table-cell;
  border: 1px solid black;
  text-align: center;
  vertical-align: middle;
  width: 40px;
  height: 40px;
}


.board .row .cell img {
  max-width: 100%;
  max-height: 100%;
}


.result-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.score-panel {
  font-size: 24px;
  margin-bottom: 30px;
}

.result {
  font-size: 24px;
  margin-bottom: 30px;
  /* color: red;  */
  font-weight: bold;
  /**yellow */
}

.result-panel button {
  margin: 5px;
  font-size: 18px;
  padding: 5px;
  border-radius: 3px;
  color: white;
  border: none;
  cursor: pointer;
  margin-right: 20px;
}

.result-panel button:first-child {
  background-color: #4caf50;
  /* green */
}

.result-panel button:last-child {
  background-color: #f44336;
  /* red */
}

/* 
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
} */
</style>