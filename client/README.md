# 🌤️ WeatherEvent Mashup - React Client

Modern React TypeScript client for the WeatherEvent Mashup API.

## ✨ Features

- **Real-time Weather Data** - Current weather conditions from OpenWeatherMap API
- **Local Events** - Cultural events from Ticketmaster API
- **Smart Recommendations** - Personalized suggestions based on weather conditions (indoor/outdoor)
- **Performance Benchmark** - Compare sequential vs parallel API calls
- **Responsive Design** - Glass morphism UI that works on mobile, tablet, and desktop

## Tech Stack

| Technology | Purpose |
|------------|---------|
| React 18 | UI Framework |
| TypeScript | Type Safety |
| Vite | Build Tool |
| Tailwind CSS | Styling |
| Lucide React | Icons |
| Axios | HTTP Client |
| React Hot Toast | Notifications |

##  Quick Start

### Prerequisites

- Node.js 18+
- Backend running on `http://localhost:8080`

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

## Project Structure
```bash
client/
├── src/
│   ├── components/     # React components (WeatherCard, EventsList, RecommendationsList)
│   ├── services/       # API service layer
│   ├── types/          # TypeScript interfaces
│   ├── App.tsx         # Main application
│   └── index.css       # Tailwind styles
├── public/             # Static assets (icons, favicon)
├── index.html          # Entry HTML
└── package.json        # Dependencies
```
## UI Components
Component	Description
WeatherCard	Displays current weather with animated icons
EventsList	Shows local cultural events by category
RecommendationsList	Weather-adaptive suggestions with priority ranking
## API Integration
The client communicates with the backend API:

* Endpoint	Method	Description
* /api/v1/agenda?city=X&date=Y	GET	Get complete agenda (parallel mode)
* /api/v1/agenda/benchmark?city=X&date=Y	GET	Compare sequential vs parallel performance
* /api/v1/health	GET	Health check
## 🌐 Environment Variables
Create a .env file:
env
VITE_API_BASE_URL=http://localhost:8080/api/v1

## CORS Configuration
Make sure the backend allows requests from the client origin:

yaml
allowed-origins: http://localhost:5173
## 📦 Available Scripts
Script	Description
npm run dev	Start development server
npm run build	Build for production
npm run preview	Preview production build
npm run lint	Run ESLint
##  Contributing
* Ensure backend is running
* Create a feature branch
* Make changes
* Test with npm run dev
* Submit a pull request

## 📄 License
MIT

##  Author
**Malaïka Ladéesse Assondji**  
**Middleware Course - Pr. BOMGNI Alain Bertrand**  
**Academic Year: 2025/2026**