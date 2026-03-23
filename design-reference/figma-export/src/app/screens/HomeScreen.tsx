import { useNavigate } from 'react-router';
import { Heart, Footprints, Moon, Activity, Droplets, Plus, Calendar } from 'lucide-react';
import { MetricCard } from '../components/MetricCard';
import { StatWidget } from '../components/StatWidget';
import { Button } from '../components/ui/button';

export function HomeScreen() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <div className="bg-card border-b border-border px-6 pt-6 pb-4">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-2xl font-bold text-foreground">Today</h1>
            <p className="text-sm text-muted-foreground">Monday, March 23</p>
          </div>
          <button
            onClick={() => navigate('/app/summary')}
            className="bg-primary/10 rounded-xl p-2.5 text-primary hover:bg-primary/20 transition-colors"
          >
            <Calendar className="w-5 h-5" />
          </button>
        </div>

        {/* Quick stats */}
        <div className="grid grid-cols-2 gap-3">
          <StatWidget
            icon={<Footprints className="w-5 h-5" />}
            label="Steps today"
            value="8,547"
            color="text-chart-1"
          />
          <StatWidget
            icon={<Heart className="w-5 h-5" />}
            label="Avg heart rate"
            value="72 bpm"
            color="text-chart-4"
          />
        </div>
      </div>

      {/* Main metrics */}
      <div className="px-6 py-6 space-y-4">
        <div className="flex items-center justify-between mb-2">
          <h2 className="text-lg font-semibold text-foreground">Health Metrics</h2>
          <Button
            onClick={() => navigate('/app/entry')}
            size="sm"
            variant="ghost"
            className="text-primary"
          >
            <Plus className="w-4 h-4 mr-1" />
            Add entry
          </Button>
        </div>

        <div className="grid gap-4">
          <MetricCard
            icon={<Footprints className="w-6 h-6" />}
            title="Steps"
            value="8,547"
            unit="steps"
            trend={{ direction: 'up', value: '+12%' }}
            source="synced"
            color="bg-chart-1"
            onClick={() => navigate('/app/metric/steps')}
          />

          <MetricCard
            icon={<Heart className="w-6 h-6" />}
            title="Heart Rate"
            value="72"
            unit="bpm"
            trend={{ direction: 'down', value: '-3%' }}
            source="synced"
            color="bg-chart-4"
            onClick={() => navigate('/app/metric/heart')}
          />

          <MetricCard
            icon={<Moon className="w-6 h-6" />}
            title="Sleep"
            value="7.5"
            unit="hours"
            trend={{ direction: 'up', value: '+8%' }}
            source="synced"
            color="bg-chart-5"
            onClick={() => navigate('/app/metric/sleep')}
          />

          <MetricCard
            icon={<Activity className="w-6 h-6" />}
            title="Active Minutes"
            value="45"
            unit="min"
            trend={{ direction: 'up', value: '+15%' }}
            source="synced"
            color="bg-chart-2"
            onClick={() => navigate('/app/metric/activity')}
          />

          <MetricCard
            icon={<Droplets className="w-6 h-6" />}
            title="Hydration"
            value="1.8"
            unit="liters"
            source="manual"
            color="bg-chart-3"
            onClick={() => navigate('/app/metric/hydration')}
          />
        </div>
      </div>

      {/* Quick actions */}
      <div className="px-6">
        <h2 className="text-lg font-semibold mb-4 text-foreground">Quick Actions</h2>
        <div className="grid grid-cols-2 gap-3">
          <button
            onClick={() => navigate('/app/goals')}
            className="bg-card border border-border rounded-2xl p-4 flex flex-col items-center gap-2 hover:shadow-md transition-shadow"
          >
            <div className="bg-primary/10 rounded-xl p-3">
              <Activity className="w-6 h-6 text-primary" />
            </div>
            <span className="text-sm font-medium text-foreground">View Goals</span>
          </button>
          
          <button
            onClick={() => navigate('/health-connect')}
            className="bg-card border border-border rounded-2xl p-4 flex flex-col items-center gap-2 hover:shadow-md transition-shadow"
          >
            <div className="bg-chart-2/10 rounded-xl p-3">
              <Heart className="w-6 h-6 text-chart-2" />
            </div>
            <span className="text-sm font-medium text-foreground">Health Connect</span>
          </button>
        </div>
      </div>
    </div>
  );
}