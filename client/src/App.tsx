import { useState } from "react";
import toast, { Toaster } from "react-hot-toast";
import {
  Search,
  TrendingUp,
  Activity,
  Zap,
  Sparkles,
  CalendarDays,
  MapPin,
  Loader2,
} from "lucide-react";

function App() {
  const [city, setCity] = useState("Paris");
  const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState(null);
  const [benchmark, setBenchmark] = useState(null);
  const [showBenchmark, setShowBenchmark] = useState(false);

  const cities = [
    "Paris",
    "London",
    "New York",
    "Tokyo",
    "Douala",
    "Dschang",
    "Yaounde",
  ];

  const handleSearch = async () => {
    if (!city.trim()) {
      toast.error("Please enter a city name");
      return;
    }

    setLoading(true);
    setShowBenchmark(false);

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/agenda?city=${encodeURIComponent(city)}&date=${date}`,
      );
      const result = await response.json();
      setData(result);
      toast.success(
        `Agenda loaded for ${city} in ${result.processingTimeMs}ms`,
      );
    } catch (error) {
      toast.error("Failed to load agenda");
    } finally {
      setLoading(false);
    }
  };

  const handleBenchmark = async () => {
    if (!city.trim()) {
      toast.error("Please enter a city name");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/agenda/benchmark?city=${encodeURIComponent(city)}&date=${date}`,
      );
      const result = await response.json();
      setBenchmark(result);
      setShowBenchmark(true);
      toast.success(`Benchmark: ${result.speedupFactor.toFixed(2)}x faster!`);
    } catch (error) {
      toast.error("Failed to run benchmark");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen py-8 px-4">
      <Toaster
        position="top-right"
        toastOptions={{ style: { background: "#1e1e2e", color: "#fff" } }}
      />

      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 backdrop-blur-sm mb-4">
            <Sparkles className="w-4 h-4 text-purple-400" />
            <span className="text-sm text-white/80">
              Weather-Based Recommendations
            </span>
          </div>
          <h1 className="text-5xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-orange-400 bg-clip-text text-transparent mb-2">
            WeatherEvent Mashup
          </h1>
          <p className="text-white/60">
            Get personalized recommendations based on weather and local events
          </p>
        </div>

        {/* Search Bar */}
        <div className="glass-card p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <label className="block text-sm text-white/60 mb-1">City</label>
              <div className="relative">
                <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/40" />
                <input
                  type="text"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="Enter city name..."
                  className="glass-input w-full pl-10"
                  list="cities"
                />
                <datalist id="cities">
                  {cities.map((c) => (
                    <option key={c} value={c} />
                  ))}
                </datalist>
              </div>
            </div>
            <div className="flex-1">
              <label className="block text-sm text-white/60 mb-1">Date</label>
              <div className="relative">
                <CalendarDays className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/40" />
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
                {loading ? (
                  <Loader2 className="w-5 h-5 animate-spin" />
                ) : (
                  <Search className="w-5 h-5" />
                )}
                Explore
              </button>
              <button
                onClick={handleBenchmark}
                disabled={loading}
                className="px-6 py-3 rounded-xl border border-white/20 text-white hover:bg-white/10 transition-all flex items-center gap-2"
              >
                <TrendingUp className="w-5 h-5" />
                Benchmark
              </button>
            </div>
          </div>
        </div>

        {/* Loading State */}
        {loading && (
          <div className="flex flex-col items-center justify-center py-20">
            <Loader2 className="w-12 h-12 text-purple-400 animate-spin mb-4" />
            <p className="text-white/60">Fetching data from APIs...</p>
          </div>
        )}

        {/* Benchmark Results */}
        {showBenchmark && benchmark && !loading && (
          <div className="glass-card p-6 mb-8 border border-green-500/30">
            <div className="flex items-center gap-2 mb-4">
              <Zap className="w-5 h-5 text-green-400" />
              <h3 className="text-lg font-semibold text-white">
                Performance Benchmark
              </h3>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center p-4 rounded-xl bg-white/5">
                <p className="text-sm text-white/50">Sequential Mode</p>
                <p className="text-2xl font-bold text-orange-400">
                  {benchmark.sequentialTimeMs}ms
                </p>
              </div>
              <div className="text-center p-4 rounded-xl bg-white/5">
                <p className="text-sm text-white/50">Parallel Mode</p>
                <p className="text-2xl font-bold text-green-400">
                  {benchmark.parallelTimeMs}ms
                </p>
              </div>
              <div className="text-center p-4 rounded-xl bg-gradient-to-r from-purple-500/20 to-pink-500/20">
                <p className="text-sm text-white/50">Speedup Factor</p>
                <p className="text-2xl font-bold text-purple-400">
                  {benchmark.speedupFactor.toFixed(2)}x
                </p>
              </div>
            </div>
            <div className="mt-4 p-3 rounded-lg bg-white/5 text-center">
              <p className="text-sm text-white/60">
                Parallel mode is{" "}
                <span className="text-green-400 font-bold">
                  {benchmark.speedupFactor.toFixed(1)}x faster
                </span>{" "}
                than sequential mode!
              </p>
            </div>
          </div>
        )}

        {/* Results */}
        {data && !loading && !showBenchmark && (
          <div className="space-y-6">
            {/* API Status */}
            <div className="flex items-center justify-center gap-3 text-sm">
              <div className="flex items-center gap-1">
                <Activity className="w-3 h-3" />
                <span className="text-white/60">API Status:</span>
              </div>
              <span
                className={`px-2 py-0.5 rounded-full text-xs ${data.apiStatus?.weatherApiAvailable ? "bg-green-500/20 text-green-400" : "bg-red-500/20 text-red-400"}`}
              >
                Weather {data.apiStatus?.weatherApiAvailable ? "✓" : "✗"}
              </span>
              <span
                className={`px-2 py-0.5 rounded-full text-xs ${data.apiStatus?.eventsApiAvailable ? "bg-green-500/20 text-green-400" : "bg-red-500/20 text-red-400"}`}
              >
                Events {data.apiStatus?.eventsApiAvailable ? "✓" : "✗"}
              </span>
              <span
                className={`px-2 py-0.5 rounded-full text-xs ${data.apiStatus?.recommendationsApiAvailable ? "bg-green-500/20 text-green-400" : "bg-red-500/20 text-red-400"}`}
              >
                Recos {data.apiStatus?.recommendationsApiAvailable ? "✓" : "✗"}
              </span>
              <span className="text-white/40 text-xs">
                ⏱️ {data.processingTimeMs}ms
              </span>
            </div>

            {/* Weather Card */}
            {data.weather && (
              <div className="glass-card p-6">
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-white/80">
                      Weather
                    </h3>
                    <p className="text-3xl font-bold text-white">
                      {data.weather.city}
                    </p>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-white/50">Temperature</p>
                    <p className="text-xl font-bold text-white">
                      {data.weather.temperature}°C
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-white/50">Feels like</p>
                    <p className="text-xl font-bold text-white">
                      {data.weather.feelsLike}°C
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-white/50">Humidity</p>
                    <p className="text-xl font-bold text-white">
                      {data.weather.humidity}%
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-white/50">Condition</p>
                    <p className="text-lg font-semibold text-white">
                      {data.weather.condition}
                    </p>
                  </div>
                </div>
                <p className="mt-4 text-white/70 text-center italic">
                  "{data.weather.description}"
                </p>
              </div>
            )}

            {/* Events */}
            <div className="glass-card p-6">
              <h3 className="text-lg font-semibold text-white/80 mb-4">
                Upcoming Events
              </h3>
              {data.events && data.events.length > 0 ? (
                <div className="space-y-3">
                  {data.events.map((event, idx) => (
                    <div key={idx} className="p-3 rounded-xl bg-white/5">
                      <h4 className="font-semibold text-white">{event.name}</h4>
                      <p className="text-sm text-white/50">{event.venue}</p>
                      <p className="text-sm text-purple-400 mt-1">
                        {event.category}
                      </p>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-white/50 text-center py-8">
                  No events found for this date
                </p>
              )}
            </div>

            {/* Recommendations */}
            <div className="glass-card p-6">
              <h3 className="text-lg font-semibold text-white/80 mb-4">
                ✨ Personalized Recommendations
              </h3>
              <div className="space-y-3">
                {data.recommendations &&
                  data.recommendations.map((rec, idx) => (
                    <div key={idx} className="p-4 rounded-xl bg-white/5">
                      <h4 className="font-semibold text-white">
                        {rec.activity}
                      </h4>
                      <p className="text-sm text-white/70">{rec.venue}</p>
                      <p className="text-sm text-white/50 mt-1">{rec.reason}</p>
                    </div>
                  ))}
              </div>
            </div>
          </div>
        )}

        {/* Empty State */}
        {!data && !loading && !showBenchmark && (
          <div className="flex flex-col items-center justify-center py-20 text-white/40">
            <Search className="w-16 h-16 mb-4 opacity-50" />
            <p className="text-lg">Enter a city and date to get started</p>
            <p className="text-sm">
              Try "Paris", "London", "Douala", or "Dschang"
            </p>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
