<template>
  <div class="error">
    <p>{{ message }}</p>
    <button @click="$emit('close')" class="close-btn">닫기</button>
  </div>
</template>

<script>
export default {
  name: 'ErrorMessage',
  props: {
    message: {
      type: String,
      required: true
    }
  },
  emits: ['close']
}
</script>

<style scoped>
.error {
  background: #e74c3c;
  color: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: rgba(255,255,255,0.2);
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 15px;
}

.close-btn:hover {
  background: rgba(255,255,255,0.3);
}
</style>