import { Event } from '../types';
import { Calendar, MapPin, Music, Palette, Utensils, AlertCircle } from 'lucide-react';

interface EventsListProps {
  events: Event[];
}

const getCategoryIcon = (category: string) => {
  const cat = category.toLowerCase();
  if (cat.includes('music')) return <Music className="w-5 h-5 text-purple-400" />;
  if (cat.includes('art')) return <Palette className="w-5 h-5 text-pink-400" />;
  if (cat.includes('food') || cat.includes('gastro')) return <Utensils className="w-5 h-5 text-orange-400" />;
  return <Calendar className="w-5 h-5 text-blue-400" />;
};

export default function EventsList({ events }: EventsListProps) {
  if (events.length === 0) {
    return (
      <div className="glass-card p-6">
        <h3 className="text-lg font-semibold text-white/80 mb-4">Upcoming Events</h3>
        <div className="flex flex-col items-center justify-center py-8 text-white/50">
          <AlertCircle className="w-12 h-12 mb-2" />
          <p>No events found for this date</p>
          <p className="text-sm">Try another date or city</p>
        </div>
      </div>
    );
  }

  return (
    <div className="glass-card p-6">
      <h3 className="text-lg font-semibold text-white/80 mb-4">Upcoming Events</h3>
      <div className="space-y-3">
        {events.map((event) => (
          <div key={event.id} className="flex items-start gap-3 p-3 rounded-xl bg-white/5 hover:bg-white/10 transition-colors">
            {getCategoryIcon(event.category)}
            <div className="flex-1">
              <h4 className="font-semibold text-white">{event.name}</h4>
              <div className="flex items-center gap-2 text-sm text-white/50">
                <MapPin className="w-3 h-3" />
                <span>{event.venue}</span>
              </div>
              <p className="text-sm text-purple-400 mt-1">{event.category}</p>
            </div>
            {event.fallback && (
              <span className="text-xs text-yellow-400">fallback</span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
