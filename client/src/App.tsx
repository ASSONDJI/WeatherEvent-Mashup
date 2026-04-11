import { useState } from 'react';
import toast, { Toaster } from 'react-hot-toast';
import { 
  Search, TrendingUp, Activity, Zap, Sparkles, CalendarDays, 
  MapPin, Loader2, CloudRain, Sun, Cloud, CloudSnow, 
  Wind, Droplets, Thermometer, Coffee, Home, Landmark, 
  Utensils, Building2, TreePine, Music, Palette, 
  AlertCircle, Compass, Award, Clock, CheckCircle2, X,
  ChevronRight, Heart, Star
} from 'lucide-react';

interface Weather {
  city: string;
  condition: string;
  temperature: number;
  feelsLike: number;
  humidity: number;
  description: string;
  fallback: boolean;
  cachedAt: string;
}

interface Event {
  id: string;
  name: string;
  venue: string;
  city: string;
  category: string;
  fallback: boolean;
}

interface Recommendation {
  id: string;
  activity: string;
  venue: string;
  reason: string;
  priority: number;
  indoor: boolean;
}

interface ApiStatus {
  weatherApiAvailable: boolean;
  eventsApiAvailable: boolean;
  recommendationsApiAvailable: boolean;
}

interface AgendaResponse {
  city: string;
  date: string;
  weather: Weather;
  events: Event[];
  recommendations: Recommendation[];
  processingTimeMs: number;
  mode: string;
  apiStatus: ApiStatus;
}

interface BenchmarkResult {
  sequentialTimeMs: number;
  parallelTimeMs: number;
  speedupFactor: number;
  sequentialResponse: AgendaResponse;
  parallelResponse: AgendaResponse;
}

function App() {
  const [city, setCity] = useState('Paris');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AgendaResponse | null>(null);
  const [benchmark, setBenchmark] = useState<BenchmarkResult | null>(null);
  const [showBenchmark, setShowBenchmark] = useState(false);

  const cities = ['Paris', 'London', 'New York', 'Tokyo', 'Douala', 'Dschang', 'Yaounde'];

  const handleSearch = async () => {
    if (!city.trim()) {
      toast.error('Please enter a city name');
      return;
    }

    setLoading(true);
    setShowBenchmark(false);
    
    try {
      const response = await fetch(`http://localhost:8080/api/v1/agenda?city=${encodeURIComponent(city)}&date=${date}`);
      const result = await response.json();
      setData(result);
      toast.success(`Agenda loaded for ${city}`);
    } catch (error) {
      toast.error('Failed to load agenda');
    } finally {
      setLoading(false);
    }
  };

  const handleBenchmark = async () => {
    if (!city.trim()) {
      toast.error('Please enter a city name');
      return;
    }

    setLoading(true);
    
    try {
      const response = await fetch(`http://localhost:8080/api/v1/agenda/benchmark?city=${encodeURIComponent(city)}&date=${date}`);
      const result = await response.json();
      setBenchmark(result);
      setShowBenchmark(true);
      toast.success(`Benchmark: ${result.speedupFactor.toFixed(2)}x faster!`);
    } catch (error) {
      toast.error('Failed to run benchmark');
    } finally {
      setLoading(false);
    }
  };

  const getWeatherIcon = (condition: string) => {
    const cond = condition.toLowerCase();
    if (cond.includes('rain') || cond.includes('drizzle')) return <CloudRain className="w-16 h-16 text-blue-400" />;
    if (cond.includes('snow')) return <CloudSnow className="w-16 h-16 text-white" />;
    if (cond.includes('clear') || cond.includes('sun')) return <Sun className="w-16 h-16 text-yellow-400" />;
    return <Cloud className="w-16 h-16 text-gray-400" />;
  };

  const getCategoryIcon = (category: string) => {
    const cat = category.toLowerCase();
    if (cat.includes('music')) return <Music className="w-5 h-5" />;
    if (cat.includes('art')) return <Palette className="w-5 h-5" />;
    if (cat.includes('food')) return <Utensils className="w-5 h-5" />;
    return <CalendarDays className="w-5 h-5" />;
  };

  const getActivityIcon = (activity: string, indoor: boolean) => {
    const act = activity.toLowerCase();
    if (act.includes('historic')) return <Landmark className="w-5 h-5" />;
    if (act.includes('cuisine') || act.includes('food')) return <Utensils className="w-5 h-5" />;
    if (act.includes('museum') || act.includes('art')) return <Building2 className="w-5 h-5" />;
    if (act.includes('park')) return <TreePine className="w-5 h-5" />;
    if (act.includes('coffee')) return <Coffee className="w-5 h-5" />;
    if (indoor) return <Home className="w-5 h-5" />;
    return <Compass className="w-5 h-5" />;
  };

  return (
    <div className="container">
      <Toaster position="top-right" toastOptions={{ style: { background: '#1e293b', color: '#fff', border: '1px solid #334155' } }} />
      
      {/* Header */}
      <div className="header animate-fade-in">
        <div className="badge">
          <Sparkles className="w-4 h-4" />
          AI-Powered Travel Assistant
        </div>
        <h1>WeatherEvent Mashup</h1>
        <p className="subtitle">Personalized recommendations based on real-time weather</p>
      </div>

      {/* Search Section */}
      <div className="search-section animate-fade-in">
        <div className="search-card">
          <div className="search-form">
            <div className="input-group">
              <label>DESTINATION</label>
              <div className="input-wrapper">
                <MapPin className="input-icon" />
                <input
                  type="text"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="Enter city name..."
                  list="cities"
                />
                <datalist id="cities">
                  {cities.map(c => <option key={c} value={c} />)}
                </datalist>
              </div>
            </div>
            <div className="input-group">
              <label>TRAVEL DATE</label>
              <div className="input-wrapper">
                <CalendarDays className="input-icon" />
                <input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                />
              </div>
            </div>
            <div className="button-group">
              <button onClick={handleSearch} disabled={loading} className="btn-primary">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                Explore
              </button>
              <button onClick={handleBenchmark} disabled={loading} className="btn-secondary">
                <TrendingUp className="w-4 h-4" />
                Benchmark
              </button>
            </div>
          </div>
          <div className="quick-cities">
            {cities.slice(0, 6).map(c => (
              <span key={c} className="city-chip" onClick={() => setCity(c)}>{c}</span>
            ))}
          </div>
        </div>
      </div>

      {/* Status Bar */}
      {data && !loading && !showBenchmark && (
        <div className="status-bar animate-fade-in">
          <div className="status-text">
            <Clock className="w-3 h-3" />
            <span>{data.processingTimeMs}ms</span>
          </div>
          <div className="status-text">
            <Activity className="w-3 h-3" />
            <span>API Status:</span>
          </div>
          <span className={`status-badge ${data.apiStatus.weatherApiAvailable ? 'status-success' : 'status-error'}`}>
            Weather {data.apiStatus.weatherApiAvailable ? '✓' : '✗'}
          </span>
          <span className={`status-badge ${data.apiStatus.eventsApiAvailable ? 'status-success' : 'status-error'}`}>
            Events {data.apiStatus.eventsApiAvailable ? '✓' : '✗'}
          </span>
          <span className={`status-badge ${data.apiStatus.recommendationsApiAvailable ? 'status-success' : 'status-error'}`}>
            Recos {data.apiStatus.recommendationsApiAvailable ? '✓' : '✗'}
          </span>
        </div>
      )}

      {/* Benchmark Card */}
      {showBenchmark && benchmark && !loading && (
        <div className="benchmark-card animate-fade-in">
          <div className="benchmark-header">
            <div className="benchmark-title">
              <Zap className="w-5 h-5 text-blue-400" />
              Performance Benchmark
            </div>
            <button className="close-btn" onClick={() => setShowBenchmark(false)}>×</button>
          </div>
          <div className="benchmark-grid">
            <div className="benchmark-item">
              <div className="benchmark-value benchmark-value-sequential">{benchmark.sequentialTimeMs}ms</div>
              <div className="benchmark-label">Sequential Mode</div>
            </div>
            <div className="benchmark-item">
              <div className="benchmark-value benchmark-value-parallel">{benchmark.parallelTimeMs}ms</div>
              <div className="benchmark-label">Parallel Mode</div>
            </div>
            <div className="benchmark-item">
              <div className="benchmark-value benchmark-value-speedup">{benchmark.speedupFactor.toFixed(2)}x</div>
              <div className="benchmark-label">Speedup Factor</div>
            </div>
          </div>
          <div className="benchmark-note">
            Parallel mode is <strong className="text-green-400">{benchmark.speedupFactor.toFixed(1)}x faster</strong> than sequential mode!
          </div>
        </div>
      )}

      {/* Loading State */}
      {loading && (
        <div className="loading animate-fade-in">
          <div className="spinner"></div>
          <p style={{ color: '#64748b' }}>Fetching weather data...</p>
        </div>
      )}

      {/* Results */}
      {data && !loading && !showBenchmark && (
        <div className="animate-fade-in">
          {/* Main Weather Card */}
          <div className="weather-main">
            <div className="weather-header">
              <div>
                <div className="weather-city">
                  <MapPin className="w-4 h-4" />
                  {data.weather.city}
                </div>
                <div className="weather-temp-large">{Math.round(data.weather.temperature)}°</div>
                <div className="weather-condition">{data.weather.condition}</div>
              </div>
              {getWeatherIcon(data.weather.condition)}
            </div>
            <div className="weather-details">
              <div className="weather-detail-item">
                <div className="weather-detail-value">{Math.round(data.weather.feelsLike)}°</div>
                <div className="weather-detail-label">Feels Like</div>
              </div>
              <div className="weather-detail-item">
                <div className="weather-detail-value">{data.weather.humidity}%</div>
                <div className="weather-detail-label">Humidity</div>
              </div>
              <div className="weather-detail-item">
                <div className="weather-detail-value">{data.weather.condition}</div>
                <div className="weather-detail-label">Condition</div>
              </div>
              <div className="weather-detail-item">
                <div className="weather-detail-value">—</div>
                <div className="weather-detail-label">Wind</div>
              </div>
            </div>
            <p className="weather-description" style={{ textAlign: 'center', marginTop: '1rem', color: '#64748b' }}>
              "{data.weather.description}"
            </p>
          </div>

          {/* Grid */}
          <div className="grid-2">
            {/* Events */}
            <div className="events-card">
              <div className="card-title">
                <CalendarDays className="w-5 h-5 text-blue-400" />
                Upcoming Events
              </div>
              {data.events.length === 0 ? (
                <div className="empty-events">
                  <AlertCircle className="w-12 h-12 mx-auto mb-3 opacity-50" />
                  <p>No events found</p>
                  <p className="text-xs mt-1">Try another date</p>
                </div>
              ) : (
                <div className="events-list">
                  {data.events.map((event, idx) => (
                    <div key={idx} className="event-item">
                      <div className="event-icon" style={{ color: '#60a5fa' }}>
                        {getCategoryIcon(event.category)}
                      </div>
                      <div className="event-info">
                        <div className="event-name">{event.name}</div>
                        <div className="event-venue">{event.venue}</div>
                        <span className="event-category">{event.category}</span>
                      </div>
                      <ChevronRight className="w-4 h-4 text-gray-600" />
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Recommendations Preview */}
            <div className="events-card">
              <div className="card-title">
                <Star className="w-5 h-5 text-yellow-500" />
                Top Pick
              </div>
              {data.recommendations.length > 0 && (
                <div className="event-item" style={{ cursor: 'pointer' }}>
                  <div className="event-icon" style={{ color: '#f59e0b' }}>
                    <Award className="w-5 h-5" />
                  </div>
                  <div className="event-info">
                    <div className="event-name">{data.recommendations[0].activity}</div>
                    <div className="event-venue">{data.recommendations[0].venue}</div>
                    <span className="event-category">Recommended</span>
                  </div>
                  <Heart className="w-4 h-4 text-pink-500" />
                </div>
              )}
            </div>
          </div>

          {/* Full Recommendations */}
          <div className="recommendations-card">
            <div className="card-title">
              <Sparkles className="w-5 h-5 text-purple-400" />
              Personalized Recommendations
            </div>
            <div className="recommendations-list">
              {data.recommendations.map((rec) => (
                <div key={rec.id} className="recommendation-item">
                  <div className={`recommendation-border ${rec.indoor ? 'border-indoor' : 'border-outdoor'}`} />
                  <div className="recommendation-content">
                    <div className="recommendation-icon">
                      {getActivityIcon(rec.activity, rec.indoor)}
                    </div>
                    <div className="recommendation-info">
                      <div className="recommendation-title">
                        {rec.activity}
                        <span className={`recommendation-badge ${rec.indoor ? 'badge-indoor' : 'badge-outdoor'}`}>
                          {rec.indoor ? 'Indoor' : 'Outdoor'}
                        </span>
                      </div>
                      <div className="recommendation-venue">{rec.venue}</div>
                      <div className="recommendation-reason">{rec.reason}</div>
                    </div>
                    <div className="recommendation-priority">
                      <span className={rec.priority === 1 ? 'priority-high' : ''}>
                        #{rec.priority}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Empty State */}
      {!data && !loading && !showBenchmark && (
        <div className="empty-state animate-fade-in">
          <Compass className="empty-icon w-16 h-16 mx-auto" />
          <p className="text-lg mb-2">Ready to explore?</p>
          <p className="text-sm">Enter a city and date to discover personalized recommendations</p>
        </div>
      )}
    </div>
  );
}

export default App;
