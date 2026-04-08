export interface Weather {
  city: string;
  condition: string;
  temperature: number;
  feelsLike: number;
  humidity: number;
  description: string;
  fallback: boolean;
  cachedAt: string;
}

export interface Event {
  id: string;
  name: string;
  venue: string;
  city: string;
  category: string;
  fallback: boolean;
}

export interface Recommendation {
  id: string;
  activity: string;
  venue: string;
  reason: string;
  priority: number;
  indoor: boolean;
}

export interface ApiStatus {
  weatherApiAvailable: boolean;
  eventsApiAvailable: boolean;
  recommendationsApiAvailable: boolean;
}

export interface AgendaResponse {
  city: string;
  date: string;
  weather: Weather;
  events: Event[];
  recommendations: Recommendation[];
  processingTimeMs: number;
  mode: string;
  apiStatus: ApiStatus;
}

export interface BenchmarkResult {
  sequentialTimeMs: number;
  parallelTimeMs: number;
  speedupFactor: number;
  sequentialResponse: AgendaResponse;
  parallelResponse: AgendaResponse;
}
