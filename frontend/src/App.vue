<template>
  <div class="app">
    <header>
      <h1>날씨 정보</h1>
    </header>
    
    <main>
      <LocationButton 
        @weather-found="handleWeatherFound"
        @error="handleLocationError"
      />
      
      <SearchBox @search="handleSearch" />
      
      <LoadingSpinner v-if="loading" />
      
      <ErrorMessage 
        v-if="error" 
        :message="error" 
        @close="clearError" 
      />
      
      <CurrentWeather 
        v-if="currentWeather && !loading" 
        :weather="currentWeather" 
      />
      
      <WeatherForecast 
        v-if="forecast.length > 0 && !loading" 
        :forecast="forecast" 
      />
    </main>
  </div>
</template>

<script>
import LocationButton from './components/LocationButton.vue'
import SearchBox from './components/SearchBox.vue'
import CurrentWeather from './components/CurrentWeather.vue'
import WeatherForecast from './components/WeatherForecast.vue'
import LoadingSpinner from './components/LoadingSpinner.vue'
import ErrorMessage from './components/ErrorMessage.vue'
import { weatherService } from './services/weatherService.js'

export default {
  name: 'App',
  components: {
    LocationButton,
    SearchBox,
    CurrentWeather,
    WeatherForecast,
    LoadingSpinner,
    ErrorMessage
  },
  data() {
    return {
      currentWeather: null,
      forecast: [],
      loading: false,
      error: null
    }
  },
  methods: {
    handleWeatherFound({ weatherData, latitude, longitude }) {
      console.log('날씨 데이터:', weatherData)
      console.log('좌표:', latitude, longitude)
      this.currentWeather = weatherData
      this.loading = false
      this.error = null
    },

    handleLocationError(errorMessage) {
      this.error = errorMessage
    },

    async handleSearch(location) {
      this.loading = true
      this.error = null
      
      try {
        const [current, forecastData] = await Promise.all([
          weatherService.getCurrentWeather(location),
          weatherService.getForecast(location)
        ])
        
        this.currentWeather = current
        this.forecast = forecastData
      } catch (err) {
        this.error = '날씨 정보를 가져올 수 없습니다. 지역명을 확인하고 다시 시도해주세요.'
        console.error('날씨 조회 실패:', err)
      } finally {
        this.loading = false
      }
    },
    
    clearError() {
      this.error = null
    }
  }
}
</script>