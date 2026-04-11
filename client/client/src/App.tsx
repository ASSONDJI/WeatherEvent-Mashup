import { useState } from 'react';
import toast, { Toaster } from 'react-hot-toast';
import { 
  Search, TrendingUp, Activity, Zap, Sparkles, CalendarDays, 
  MapPin, Loader2, CloudRain, Sun, Cloud, CloudSnow, 
  Wind, Droplets, Thermometer, Coffee, Home, Landmark, 
  Utensils, Building2, TreePine, Music, Palette, 
  AlertCircle, Compass, Award, Clock, CheckCircle2 
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

// Composant météo amélioré
function WeatherCard({ weather, isLoading }: { weather: Weather; isLoading: boolean }) {
  if (isLoading) {
    return (
      <div className="glass-card p-6 animate-pulse">
        <div className="h-32 bg-dark-400/50 rounded-xl"></div>
      </div>
    );
  }

  const getWeatherIcon = () => {
    const cond = weather.condition.toLowerCase();
    if (cond.includes('rain') || cond.includes('drizzle')) return <CloudRain className="w-14 h-14 text-blue-400" />;
    if (cond.includes('snow')) return <CloudSnow className="w-14 h-14 text-white" />;
    if (cond.includes('clear') || cond.includes('sun')) return <Sun className="w-14 h-14 text-yellow-400" />;
    return <Cloud className="w-14 h-14 text-gray-400" />;
  };

  return (
    <div className="glass-card p-6 animate-slide-up">
      <div className="flex items-center justify-between mb-6">
        <div>
          <div className="flex items-center gap-2 text-primary-400 mb-1">
            <MapPin className="w-4 h-4" />
            <span className="text-xs font-medium uppercase tracking-wide">Current Weather</span>
          </div>
          <h2 className="text-3xl font-bold text-white">{weather.city}</h2>
        </div>
        {getWeatherIcon()}
      </div>
      
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div className="bg-white/5 rounded-xl p-4 text-center">
          <Thermometer className="w-5 h-5 text-orange-400 mx-auto mb-2" />
          <p className="text-2xl font-bold text-white">{Math.round(weather.temperature)}°C</p>
          <p className="text-xs text-gray-400">Temperature</p>
        </div>
        <div className="bg-white/5 rounded-xl p-4 text-center">
          <Droplets className="w-5 h-5 text-blue-400 mx-auto mb-2" />
          <p className="text-2xl font-bold text-white">{weather.humidity}%</p>
          <p className="text-xs text-gray-400">Humidity</p>
        </div>
        <div className="bg-white/5 rounded-xl p-4 text-center">
          <Wind className="w-5 h-5 text-teal-400 mx-auto mb-2" />
          <p className="text-2xl font-bold text-white">{Math.round(weather.feelsLike)}°C</p>
          <p className="text-xs text-gray-400">Feels like</p>
        </div>
        <div className="bg-white/5 rounded-xl p-4 text-center">
          <Activity className="w-5 h-5 text-purple-400 mx-auto mb-2" />
          <p className="text-sm font-semibold text-white">{weather.condition}</p>
          <p className="text-xs text-gray-400">Condition</p>
        </div>
      </div>
      
      <p className="text-center text-gray-300 italic text-sm">"{weather.description}"</p>
    </div>
  );
}

// Composant événements amélioré
function EventsList({ events, isLoading }: { events: Event[]; isLoading: boolean }) {
  if (isLoading) {
    return (
      <div className="glass-card p-6">
        <div className="h-6 w-32 bg-dark-400/50 rounded mb-4 animate-pulse"></div>
        <div className="space-y-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-20 bg-dark-400/50 rounded-xl animate-pulse"></div>
          ))}
        </div>
      </div>
    );
  }

  const getCategoryIcon = (category: string) => {
    const cat = category.toLowerCase();
    if (cat.includes('music')) return <Music className="w-5 h-5 text-purple-400" />;
    if (cat.includes('art')) return <Palette className="w-5 h-5 text-pink-400" />;
    if (cat.includes('food')) return <Utensils className="w-5 h-5 text-orange-400" />;
    return <CalendarDays className="w-5 h-5 text-blue-400" />;
  };

  return (
    <div className="glass-card p-6">
      <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
        <CalendarDays className="w-5 h-5 text-primary-400" />
        Upcoming Events
      </h3>
      {events.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-12 text-gray-400">
          <AlertCircle className="w-12 h-12 mb-3 opacity-50" />
          <p className="text-sm">No events found for this date</p>
          <p className="text-xs mt-1">Try another date or city</p>
        </div>
      ) : (
        <div className="space-y-3">
          {events.map((event, idx) => (
            <div 
              key={idx} 
              className="flex items-start gap-3 p-3 rounded-xl bg-white/5 hover:bg-white/10 transition-all duration-200 group cursor-pointer"
            >
              <div className="p-2 rounded-lg bg-primary-500/20">
                {getCategoryIcon(event.category)}
              </div>
              <div className="flex-1">
                <h4 className="font-medium text-white group-hover:text-primary-400 transition-colors">
                  {event.name}
                </h4>
                <p className="text-sm text-gray-400">{event.venue}</p>
                <span className="inline-block text-xs text-primary-400 mt-1">{event.category}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

// Composant recommandations amélioré
function RecommendationsList({ recommendations, isLoading }: { recommendations: Recommendation[]; isLoading: boolean }) {
  if (isLoading) {
    return (
      <div className="glass-card p-6">
        <div className="h-6 w-40 bg-dark-400/50 rounded mb-4 animate-pulse"></div>
        <div className="grid gap-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-24 bg-dark-400/50 rounded-xl animate-pulse"></div>
          ))}
        </div>
      </div>
    );
  }

  const getActivityIcon = (activity: string, indoor: boolean) => {
    const act = activity.toLowerCase();
    if (act.includes('historic')) return <Landmark className="w-5 h-5 text-amber-400" />;
    if (act.includes('cuisine') || act.includes('food')) return <Utensils className="w-5 h-5 text-orange-400" />;
    if (act.includes('museum') || act.includes('art')) return <Building2 className="w-5 h-5 text-purple-400" />;
    if (act.includes('park')) return <TreePine className="w-5 h-5 text-emerald-400" />;
    if (act.includes('coffee')) return <Coffee className="w-5 h-5 text-amber-400" />;
    if (indoor) return <Home className="w-5 h-5 text-blue-400" />;
    return <Compass className="w-5 h-5 text-teal-400" />;
  };

  const getPriorityBadge = (priority: number) => {
    if (priority === 1) return <Award className="w-4 h-4 text-yellow-400" />;
    if (priority <= 3) return <Sparkles className="w-4 h-4 text-primary-400" />;
    return <CheckCircle2 className="w-4 h-4 text-gray-400" />;
  };

  const sorted = [...recommendations].sort((a, b) => a.priority - b.priority);

  return (
    <div className="glass-card p-6">
      <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
        <Sparkles className="w-5 h-5 text-primary-400" />
        Personalized Recommendations
      </h3>
      <div className="grid gap-3">
        {sorted.map((rec) => (
          <div 
            key={rec.id} 
            className="group relative overflow-hidden rounded-xl bg-gradient-to-r from-white/5 to-transparent hover:from-white/10 transition-all duration-300 cursor-pointer"
          >
            <div className={`absolute top-0 left-0 w-1 h-full ${rec.indoor ? 'bg-blue-500' : 'bg-emerald-500'} rounded-l`} />
            <div className="flex items-start gap-4 p-4">
              <div className="p-2 rounded-lg bg-dark-400/50 group-hover:bg-primary-500/20 transition-all">
                {getActivityIcon(rec.activity, rec.indoor)}
              </div>
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <h4 className="font-semibold text-white group-hover:text-primary-400 transition-colors">
                    {rec.activity}
                  </h4>
                  <span className={`text-xs px-2 py-0.5 rounded-full ${rec.indoor ? 'bg-blue-500/20 text-blue-300' : 'bg-emerald-500/20 text-emerald-300'}`}>
                    {rec.indoor ? 'Indoor' : 'Outdoor'}
                  </span>
                  <div className="ml-auto flex items-center gap-1">
                    {getPriorityBadge(rec.priority)}
                    <span className="text-xs text-gray-400">Priority {rec.priority}</span>
                  </div>
                </div>
                <p className="text-sm text-gray-300">{rec.venue}</p>
                <p className="text-xs text-gray-400 mt-1">{rec.reason}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// Composant benchmark
function BenchmarkCard({ benchmark, onClose }: { benchmark: BenchmarkResult; onClose: () => void }) {
  return (
    <div className="glass-card p-6 mb-8 border border-primary-500/30 animate-slide-down">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Zap className="w-5 h-5 text-primary-400" />
          <h3 className="text-lg font-semibold text-white">Performance Benchmark</h3>
        </div>
        <button onClick={onClose} className="text-gray-400 hover:text-white transition-colors">×</button>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="text-center p-4 rounded-xl bg-dark-300/50">
          <p className="text-sm text-gray-400 mb-1">Sequential Mode</p>
          <p className="text-2xl font-bold text-orange-400">{benchmark.sequentialTimeMs}ms</p>
        </div>
        <div className="text-center p-4 rounded-xl bg-dark-300/50">
          <p className="text-sm text-gray-400 mb-1">Parallel Mode</p>
          <p className="text-2xl font-bold text-emerald-400">{benchmark.parallelTimeMs}ms</p>
        </div>
        <div className="text-center p-4 rounded-xl bg-gradient-to-r from-primary-500/20 to-purple-500/20">
          <p className="text-sm text-gray-400 mb-1">Speedup Factor</p>
          <p className="text-2xl font-bold text-primary-400">{benchmark.speedupFactor.toFixed(2)}x</p>
        </div>
      </div>
      <div className="mt-4 p-3 rounded-lg bg-primary-500/10 text-center">
        <p className="text-sm text-gray-300">
          Parallel mode is <span className="text-emerald-400 font-bold">{benchmark.speedupFactor.toFixed(1)}x faster</span> than sequential mode!
        </p>
      </div>
    </div>
  );
}

function App() {
  const [city, setCity] = useState('Paris');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<AgendaResponse | null>(null);
  const [benchmark, setBenchmark] = useState<BenchmarkResult | null>(null);
  const [showBenchmark, setShowBenchmark] = useState(false);

  const cities = ['Paris', 'London', 'New York', 'Tokyo', 'Douala', 'Dschang', 'Yaounde', 'Berlin', 'Rome', 'Madrid'];

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
      toast.success(`Agenda loaded for ${city} in ${result.processingTimeMs}ms`);
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

  return (
    <div className="min-h-screen py-8 px-4">
      <Toaster position="top-right" toastOptions={{ style: { background: '#1e1e2e', color: '#fff', border: '1px solid #3f3f46' } }} />
      
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="text-center mb-10 animate-fade-in">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-primary-500/10 border border-primary-500/20 mb-5">
            <Sparkles className="w-4 h-4 text-primary-400" />
            <span className="text-sm text-primary-300 font-medium">AI-Powered Travel Assistant</span>
          </div>
          <h1 className="text-5xl md:text-6xl font-bold bg-gradient-to-r from-primary-400 via-purple-400 to-pink-400 bg-clip-text text-transparent mb-3">
            WeatherEvent Mashup
          </h1>
          <p className="text-gray-400 max-w-md mx-auto">
            Get personalized recommendations based on real-time weather and local events
          </p>
        </div>

        {/* Search Bar */}
        <div className="glass-card p-5 mb-8 animate-fade-in">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-300 mb-1">Destination</label>
              <div className="relative">
                <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500" />
                <input
                  type="text"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="Enter city name..."
                  className="glass-input w-full pl-10"
                  list="cities"
                />
                <datalist id="cities">
                  {cities.map(c => <option key={c} value={c} />)}
                </datalist>
              </div>
            </div>
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-300 mb-1">Travel Date</label>
              <div className="relative">
                <CalendarDays className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500" />
                <input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="glass-input w-full pl-10"
                />
              </div>
            </div>
            <div className="flex gap-3 items-end">
              <button
                onClick={handleSearch}
                disabled={loading}
                className="btn-primary flex items-center gap-2"
              >
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                Explore
              </button>
              <button
                onClick={handleBenchmark}
                disabled={loading}
                className="btn-secondary flex items-center gap-2"
              >
                <TrendingUp className="w-4 h-4" />
                Benchmark
              </button>
            </div>
          </div>
        </div>

        {/* Loading State avec skeleton */}
        {loading && (
          <div className="space-y-6 animate-fade-in">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div className="glass-card p-6">
                <div className="h-64 bg-dark-400/30 rounded-xl animate-pulse"></div>
              </div>
              <div className="glass-card p-6">
                <div className="h-64 bg-dark-400/30 rounded-xl animate-pulse"></div>
              </div>
            </div>
            <div className="glass-card p-6">
              <div className="h-96 bg-dark-400/30 rounded-xl animate-pulse"></div>
            </div>
          </div>
        )}

        {/* Benchmark Results */}
        {showBenchmark && benchmark && !loading && (
          <BenchmarkCard benchmark={benchmark} onClose={() => setShowBenchmark(false)} />
        )}

        {/* Results */}
        {data && !loading && !showBenchmark && (
          <div className="space-y-6 animate-fade-in">
            {/* API Status */}
            <div className="flex flex-wrap items-center justify-center gap-3 text-sm">
              <Clock className="w-3 h-3 text-gray-400" />
              <span className="text-gray-400">Response time: <span className="text-white font-mono">{data.processingTimeMs}ms</span></span>
              <div className="w-px h-4 bg-dark-400"></div>
              <div className="flex items-center gap-2">
                <Activity className="w-3 h-3 text-gray-400" />
                <span className="text-gray-400">API Status:</span>
              </div>
              <span className={`status-badge ${data.apiStatus.weatherApiAvailable ? 'status-badge-success' : 'status-badge-error'}`}>
                Weather {data.apiStatus.weatherApiAvailable ? '✓' : '✗'}
              </span>
              <span className={`status-badge ${data.apiStatus.eventsApiAvailable ? 'status-badge-success' : 'status-badge-error'}`}>
                Events {data.apiStatus.eventsApiAvailable ? '✓' : '✗'}
              </span>
              <span className={`status-badge ${data.apiStatus.recommendationsApiAvailable ? 'status-badge-success' : 'status-badge-error'}`}>
                Recos {data.apiStatus.recommendationsApiAvailable ? '✓' : '✗'}
              </span>
            </div>

            {/* Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <WeatherCard weather={data.weather} isLoading={false} />
              <EventsList events={data.events} isLoading={false} />
            </div>
            
            <RecommendationsList recommendations={data.recommendations} isLoading={false} />
          </div>
        )}

        {/* Empty State */}
        {!data && !loading && !showBenchmark && (
          <div className="flex flex-col items-center justify-center py-20 text-center animate-fade-in">
            <div className="p-6 rounded-full bg-dark-300/50 mb-6">
              <Compass className="w-16 h-16 text-gray-500" />
            </div>
            <p className="text-xl text-gray-400 mb-2">Ready to explore?</p>
            <p className="text-sm text-gray-500">Enter a city and date to discover personalized recommendations</p>
            <div className="flex gap-2 mt-6">
              {['Paris', 'London', 'Tokyo', 'Douala'].map(c => (
                <button
                  key={c}
                  onClick={() => setCity(c)}
                  className="px-3 py-1 rounded-full bg-dark-300/50 text-gray-400 text-sm hover:bg-dark-300 hover:text-white transition-colors"
                >
                  {c}
                </button>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
