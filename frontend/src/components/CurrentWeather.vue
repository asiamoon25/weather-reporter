<template>
  <div class="current-weather">
    <h2>현재 날씨</h2>
    <div v-if="weather.regionName" class="location-info">
      📍 {{ weather.regionName }}
    </div>
    <div class="weather-info">
      <div class="temperature">
        {{ Math.round(weather.temperature) }}°C
      </div>
      <div class="weather-desc">
        {{ getWeatherDescription(weather.weatherCode) }}
      </div>
      <div class="details">
        <div class="detail-item">
          <span>습도:</span>
          <span>{{ weather.humidity || '-' }}%</span>
        </div>
        <div class="detail-item">
          <span>풍속:</span>
          <span>{{ weather.windSpeed || '-' }}m/s</span>
        </div>
        <div class="detail-item">
          <span>강수확률:</span>
          <span>{{ weather.rainProbability || '-' }}%</span>
        </div>
        <div class="detail-item">
          <span>강수량:</span>
          <span>{{ weather.rainfallText || '강수 없음' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CurrentWeather',
  props: {
    weather: {
      type: Object,
      required: true
    }
  },
  methods: {
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
.current-weather {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

.current-weather h2 {
  margin-bottom: 20px;
  color: #2d3436;
  border-bottom: 2px solid #74b9ff;
  padding-bottom: 10px;
}

.location-info {
  font-size: 1.1rem;
  color: #636e72;
  margin-bottom: 15px;
  font-weight: 500;
}

.weather-info {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 20px;
  align-items: center;
}

.temperature {
  font-size: 4rem;
  font-weight: bold;
  color: #74b9ff;
  text-align: center;
}

.weather-desc {
  font-size: 1.5rem;
  color: #636e72;
  margin-bottom: 20px;
}

.details {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

@media (max-width: 600px) {
  .weather-info {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .temperature {
    font-size: 3rem;
  }
}
</style>