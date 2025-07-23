const API_BASE = 'http://localhost:8080/api/weather'

export const weatherService = {
  async getCurrentWeather(location) {
    try {
      const response = await fetch(`${API_BASE}/current?location=${encodeURIComponent(location)}`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('현재 날씨 조회 실패:', error)
      throw error
    }
  },

  async getForecast(location) {
    try {
      const response = await fetch(`${API_BASE}/forecast?location=${encodeURIComponent(location)}`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('예보 조회 실패:', error)
      throw error
    }
  },

  async getCurrentWeatherByCoords(latitude, longitude) {
    try {
      const response = await fetch(`${API_BASE}/current?lat=${latitude}&lon=${longitude}`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('위치 기반 현재 날씨 조회 실패:', error)
      throw error
    }
  },

  async getForecastByCoords(latitude, longitude) {
    try {
      const response = await fetch(`${API_BASE}/forecast?lat=${latitude}&lon=${longitude}`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('위치 기반 예보 조회 실패:', error)
      throw error
    }
  }
}