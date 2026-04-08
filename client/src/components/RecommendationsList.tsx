import { Recommendation } from '../types';
import { Home, Coffee, Landmark, Utensils, Building2, TreePine, Sparkles } from 'lucide-react';

interface RecommendationsListProps {
  recommendations: Recommendation[];
}

const getActivityIcon = (activity: string, indoor: boolean) => {
  const act = activity.toLowerCase();
  if (act.includes('historic') || act.includes('center')) return <Landmark className="w-5 h-5 text-amber-400" />;
  if (act.includes('cuisine') || act.includes('food')) return <Utensils className="w-5 h-5 text-orange-400" />;
  if (act.includes('museum') || act.includes('art')) return <Building2 className="w-5 h-5 text-purple-400" />;
  if (act.includes('park') || act.includes('garden')) return <TreePine className="w-5 h-5 text-green-400" />;
  if (act.includes('coffee') || act.includes('cafe')) return <Coffee className="w-5 h-5 text-amber-400" />;
  if (indoor) return <Home className="w-5 h-5 text-blue-400" />;
  return <Sparkles className="w-5 h-5 text-yellow-400" />;
};

const getPriorityColor = (priority: number) => {
  switch (priority) {
    case 1: return 'bg-gradient-to-r from-red-500 to-orange-500';
    case 2: return 'bg-gradient-to-r from-orange-500 to-yellow-500';
    case 3: return 'bg-gradient-to-r from-yellow-500 to-green-500';
    default: return 'bg-gradient-to-r from-blue-500 to-purple-500';
  }
};

export default function RecommendationsList({ recommendations }: RecommendationsListProps) {
  const sorted = [...recommendations].sort((a, b) => a.priority - b.priority);

  return (
    <div className="glass-card p-6">
      <h3 className="text-lg font-semibold text-white/80 mb-4">✨ Personalized Recommendations</h3>
      <div className="space-y-3">
        {sorted.map((rec) => (
          <div key={rec.id} className="relative overflow-hidden rounded-xl bg-white/5 hover:bg-white/10 transition-all duration-300">
            <div className={`absolute top-0 left-0 w-1 h-full ${getPriorityColor(rec.priority)}`} />
            <div className="flex items-start gap-3 p-4">
              <div className="flex-shrink-0">
                {getActivityIcon(rec.activity, rec.indoor)}
              </div>
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <h4 className="font-semibold text-white">{rec.activity}</h4>
                  {rec.indoor ? (
                    <span className="text-xs px-2 py-0.5 rounded-full bg-blue-500/20 text-blue-300">Indoor</span>
                  ) : (
                    <span className="text-xs px-2 py-0.5 rounded-full bg-green-500/20 text-green-300">Outdoor</span>
                  )}
                </div>
                <p className="text-sm text-white/70">{rec.venue}</p>
                <p className="text-sm text-white/50 mt-1">{rec.reason}</p>
              </div>
              <div className="flex-shrink-0">
                <div className={`w-8 h-8 rounded-full ${getPriorityColor(rec.priority)} flex items-center justify-center text-white font-bold text-sm`}>
                  {rec.priority}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
