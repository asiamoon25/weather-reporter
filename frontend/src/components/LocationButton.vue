<template>
  <div class="location-section">
    <button 
      @click="getCurrentLocation" 
      :disabled="loading"
      class="location-btn"
    >
      <span class="location-icon">📍</span>
      {{ loading ? '위치 확인 중...' : '현재 위치 날씨' }}
    </button>
    
    <div class="divider">
      <span>또는</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LocationButton',
  data() {
    return {
      loading: false
    }
  },
  methods: {
    async getCurrentLocation() {
      if (!navigator.geolocation) {
        this.$emit('error', '브라우저에서 위치 서비스를 지원하지 않습니다.')
        return
      }

      this.loading = true

      const options = {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 300000 // 5분
      }

      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const { latitude, longitude } = position.coords
          
          // 좌표로 날씨 데이터 가져오기
          try {
            const response = await fetch(`/api/weather/current?lat=${latitude}&lon=${longitude}`)
            const weatherData = await response.json()
            this.$emit('weather-found', { weatherData, latitude, longitude })
          } catch (error) {
            console.error('날씨 데이터 가져오기 실패:', error)
            this.$emit('error', '날씨 정보를 가져올 수 없습니다.')
          }
          
          this.loading = false
        },
        (error) => {
          this.loading = false
          let errorMessage = '위치를 가져올 수 없습니다.'
          console.error(error.code);
          switch (error.code) {
            case error.PERMISSION_DENIED:
              errorMessage = '위치 권한이 거부되었습니다. 브라우저 설정에서 위치 권한을 허용해주세요.'
              break
            case error.POSITION_UNAVAILABLE:
              errorMessage = '위치 정보를 사용할 수 없습니다.'
              break
            case error.TIMEOUT:
              errorMessage = '위치 요청 시간이 초과되었습니다.'
              break
          }
          
          this.$emit('error', errorMessage)
        },
        options
      )
    }
  },
  mounted() {
    // 페이지 로드 시 자동으로 현재 위치 날씨 가져오기
    this.getCurrentLocation()
  }
}
</script>

<style scoped>
.location-section {
  margin-bottom: 20px;
  text-align: center;
}

.location-btn {
  background: #6c5ce7;
  color: white;
  border: none;
  border-radius: 25px;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  transition: background 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
}

.location-btn:hover:not(:disabled) {
  background: #5f3dc4;
}

.location-btn:disabled {
  background: #a29bfe;
  cursor: not-allowed;
}

.location-icon {
  font-size: 18px;
}

.divider {
  margin: 20px 0;
  position: relative;
  color: white;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: rgba(255,255,255,0.3);
  z-index: 1;
}

.divider span {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  padding: 0 15px;
  position: relative;
  z-index: 2;
}
</style>