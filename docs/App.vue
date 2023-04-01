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
      result: 'You: 0, Opponent: 0'
    };
  },
  mounted() {
    setInterval(() => {
      this.refreshMatchingList();
    }, 5000);
  },
  methods: {
    pickUsername() {
      // TODO: Implement pickUsername method
    },
    matchWithPlayer(player) {
      // TODO: Implement matchWithPlayer method
    },
    matchWithRandomPlayer() {
      // TODO: Implement matchWithRandomPlayer method
    },
    refreshMatchingList() {
      // TODO: Implement refreshMatchingList method
    },
    placeStone(row, col) {
      // TODO: Implement placeStone method
    },
    startNewRound() {
      // TODO: Implement startNewRound method
    },
    exitGame() {
      // TODO: Implement exitGame method
    }
  },
  computed: {
    /* Computed properties for calculating board-related data */
  },
  watch: {
    /* Watchers for data changes */
  }
};
</script>

<style> /* CSS styles for the app */
</style>