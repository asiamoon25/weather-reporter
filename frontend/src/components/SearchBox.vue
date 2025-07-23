<template>
  <div class="search-box">
    <input 
      type="text" 
      v-model="location"
      @keyup.enter="search"
      placeholder="지역을 입력하세요 (예: 서울, 부산)"
      class="location-input"
    >
    <button @click="search" class="search-btn">검색</button>
  </div>
</template>

<script>
export default {
  name: 'SearchBox',
  data() {
    return {
      location: ''
    }
  },
  methods: {
    search() {
      if (this.location.trim()) {
        this.$emit('search', this.location.trim())
      }
    }
  }
}
</script>

<style scoped>
.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  justify-content: center;
}

.location-input {
  padding: 12px 16px;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  width: 300px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  outline: none;
}

.search-btn {
  padding: 12px 24px;
  background: #00b894;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  transition: background 0.3s ease;
}

.search-btn:hover {
  background: #00a085;
}

@media (max-width: 600px) {
  .search-box {
    flex-direction: column;
    align-items: center;
  }
  
  .location-input {
    width: 100%;
    max-width: 300px;
  }
}
</style>