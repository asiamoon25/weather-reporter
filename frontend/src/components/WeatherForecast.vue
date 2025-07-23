<template>
  <div class="forecast">
    <h2>3일 예보</h2>
    <div class="forecast-list">
      <div 
        v-for="item in forecast.slice(0, 3)" 
        :key="item.date"
        class="forecast-item"
      >
        <div class="forecast-date">{{ formatDate(item.date) }}</div>
        <div class="forecast-temp">
          {{ Math.round(item.maxTemp) }}°/{{ Math.round(item.minTemp) }}°
        </div>
        <div class="forecast-desc">
          {{ getWeatherDescription(item.weatherCode) }}
        </div>
        <div class="forecast-rain">
          강수: {{ item.rainProbability || 0 }}%
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WeatherForecast',
  props: {
    forecast: {
      type: Array,
      required: true
    }
  },
  methods: {
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        month: 'short',
        day: 'numeric',
        weekday: 'short'
      })
    },
    
    getWeatherDescription(code) {
      const weatherCodes = {
        1: '맑음',
        2: '구름조금',
        3: '구름많음',
        4: '흐림',
        5: '비',
        6: '비/눈',
        7: '눈'
      }
      
      return weatherCodes[code] || '정보없음'
    }
  }
}
</script>

<style scoped>
.forecast {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

.forecast h2 {
  margin-bottom: 20px;
  color: #2d3436;
  border-bottom: 2px solid #74b9ff;
  padding-bottom: 10px;
}

.forecast-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.forecast-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
}

.forecast-date {
  font-weight: bold;
  margin-bottom: 10px;
  color: #2d3436;
}

.forecast-temp {
  font-size: 1.5rem;
  color: #74b9ff;
  margin: 10px 0;
}

.forecast-desc {
  color: #636e72;
  font-size: 0.9rem;
}

.forecast-rain {
  color: #636e72;
  font-size: 0.8rem;
  margin-top: 5px;
}
</style>