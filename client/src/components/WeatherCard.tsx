import type { Weather } from '../types';
import { Sun, Cloud, CloudRain, CloudSnow, Wind, Droplets, Thermometer } from 'lucide-react';

interface WeatherCardProps {
  weather: Weather;
}

const getWeatherIcon = (condition: string) => {
  const cond = condition.toLowerCase();
  if (cond.includes('sun') || cond.includes('clear')) return <Sun className="w-16 h-16 text-yellow-400" />;
  if (cond.includes('rain') || cond.includes('drizzle')) return <CloudRain className="w-16 h-16 text-blue-400" />;
  if (cond.includes('snow')) return <CloudSnow className="w-16 h-16 text-white" />;
  return <Cloud className="w-16 h-16 text-gray-400" />;
};

export default function WeatherCard({ weather }: WeatherCardProps) {
  return (
    <div className="glass-card p-6">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-white/80">Weather</h3>
          <p className="text-3xl font-bold text-white">{weather.city}</p>
        </div>
        {getWeatherIcon(weather.condition)}
      </div>
      
      <div className="grid grid-cols-2 gap-4">
        <div className="flex items-center gap-2">
          <Thermometer className="w-5 h-5 text-red-400" />
          <div>
            <p className="text-sm text-white/50">Temperature</p>
            <p className="text-xl font-bold text-white">{weather.temperature}°C</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Thermometer className="w-5 h-5 text-orange-400" />
          <div>
            <p className="text-sm text-white/50">Feels like</p>
            <p className="text-xl font-bold text-white">{weather.feelsLike}°C</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Droplets className="w-5 h-5 text-blue-400" />
          <div>
            <p className="text-sm text-white/50">Humidity</p>
            <p className="text-xl font-bold text-white">{weather.humidity}%</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Wind className="w-5 h-5 text-gray-400" />
          <div>
            <p className="text-sm text-white/50">Condition</p>
            <p className="text-lg font-semibold text-white">{weather.condition}</p>
          </div>
        </div>
      </div>
      
      <p className="mt-4 text-white/70 text-center italic">"{weather.description}"</p>
      
      {weather.fallback && (
        <p className="mt-2 text-yellow-400 text-sm text-center">⚠️ Using fallback data</p>
      )}
    </div>
  );
}