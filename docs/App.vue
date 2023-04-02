<template>
  <div class="app">
    <div class="home" v-if="!matchedPlayer">
      <div class="input-box">
        <label for="username">Username: </label>
        <input type="text" id="username" v-model="username">
        <button @click="pickUsername">Pick</button>
      </div>
      <div class="message">{{ notification }}</div>
      <div class="matching-list">
        <table>
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
              <td>{{ player.waiting_time }}</td>
              <td>{{ player.username }}</td>
              <td><button @click="matchWithPlayer(player)">Match</button></td>
              <td></td>
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
            <img :src="cell">
          </div>
        </div>
        <div class="result-panel">
          <div class="result">{{ result }}</div>
          <button @click="startNewRound"> Start New Round </button>
          <button @click="exitGame"> Exit Game </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      matchedPlayer: null,
      notification: '',
      matchingList: [],
      yourTurn: false,
      remainingTime: 30,
      board: [], // Initialize board with empty array of 15x15
      result: 'You: 0, Opponent: 0',
      gameId: null
    };
  },
  mounted() {
    setInterval(() => {
      this.refreshMatchingList();
    }, 5000);
  },
  methods: {
    async pickUsername() {
      try {
        const response = await axios.post('/api/match', {
          username: this.username
        });
        if (response.status === 200) {
          this.notification = 'Successfully joined the matching list';
          this.matchedPlayer = null;
          this.yourTurn = false;
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },
    async matchWithPlayer(player) {
      try {
        const response = await axios.post('/api/match', {
          username: this.username,
          opponent_id: player.id
        });
        if (response.status === 200) {
          this.notification = 'Successfully matched with a player';
          this.matchedPlayer = response.data;
          this.board = response.data.board;
          this.yourTurn = response.data.your_turn;
          this.remainingTime = response.data.remaining_time;
          this.gameId = response.data.id;
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },
    async matchWithRandomPlayer() {
      try {
        const response = await axios.post('/api/match/dice', {
          username: this.username
        });
        if (response.status === 200) {
          this.notification = 'Successfully matched with a player';
          this.matchedPlayer = response.data;
          this.board = response.data.board;
          this.yourTurn = response.data.your_turn;
          this.remainingTime = response.data.remaining_time;
          this.gameId = response.data.id;
        }
      } catch (error) {
        this.notification = error.response.data.msg;
      }
    },
    async refreshMatchingList() {
      try {
        const response = await axios.get('/api/match');
        if (response.status === 200) {
          this.matchingList = response.data;
        }
      } catch (error) {
        console.error(error);
      }
    },
    async placeStone(row, col) {
      try {
        const response = await axios.post('/api/game', {
          id: this.gameId,
          row,
          col
        });
        if (response.status === 200) {
          this.board = response.data.board;
          this.yourTurn = response.data.your_turn;
          this.remainingTime = response.data.remaining_time;
          this.result = `You: ${response.data.player1_score}, Opponent: ${response.data.player2_score}`;

          // set the class of the cell based on the player's turn                                                                                                                                                        
          const cellClass = response.data.turn === 1 ? 'white' : 'black';
          const cell = document.querySelector(`.board .row:nth-child(${row + 1}) .cell:nth-child(${col + 1})`);
          cell.classList.add(cellClass);
          // const cellImage = response.data.player_turn === 1 ? 'assets/whiteStone.gif' : 'assets/blackStone.gif';
          // this.board[row][col] = cellImage;

          // check for game over
          if (response.data.result !== null) {
            if (response.data.result === 0) {
              this.notification = "It's a tie!";
            } else if (
              (response.data.result === 1 && response.data.your_turn) ||
              (response.data.result === 2 && !response.data.your_turn)
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
        await axios.post('/api/game', {
          id: this.gameId,
          clear: true
        });
        this.board = this.createEmptyBoard();
        this.yourTurn = false;
        this.remainingTime = 30;
        this.result = 'You: 0, Opponent: 0';
        this.notification = '';
      } catch (error) {
        console.error(error);
      }
    },
    async exitGame() {
      try {
        await axios.post('/api/game/exit', {
          id: this.gameId
        });
        this.matchedPlayer = null;
        this.gameId = null;
        this.board = this.createEmptyBoard();
        this.result = 'You: 0, Opponent: 0';
        this.notification = '';
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
  grid-template-columns: repeat(15, 50px);
  grid-template-rows: repeat(15, 50px);
  margin-bottom: 20px;
}

.board .row {
  display: flex;
}

.board .cell {
  width: 50px;
  height: 50px;
}

.board .cell img {
  max-width: 100%;
}

.board .cell.white {
  background-image: url('assets/whiteStone.gif');
}

.board .cell.black {
  background-image: url('assets/blackStone.gif');
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
</style>