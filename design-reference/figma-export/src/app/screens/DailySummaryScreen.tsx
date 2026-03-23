import { useNavigate } from 'react-router';
import { ArrowLeft, CheckCircle2, Circle, Footprints, Heart, Moon, Activity, Droplets, Calendar } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const weekSummary = [
  { day: 'Mon', score: 85 },
  { day: 'Tue', score: 78 },
  { day: 'Wed', score: 92 },
  { day: 'Thu', score: 88 },
  { day: 'Fri', score: 95 },
  { day: 'Sat', score: 90 },
  { day: 'Sun', score: 87 },
];

const todayMetrics = [
  {
    icon: Footprints,
    label: 'Steps',
    value: '8,547',
    target: '10,000',
    achieved: false,
    color: 'text-chart-1',
  },
  {
    icon: Heart,
    label: 'Heart Rate',
    value: '72 bpm',
    target: 'Normal',
    achieved: true,
    color: 'text-chart-4',
  },
  {
    icon: Moon,
    label: 'Sleep',
    value: '7.5 hrs',
    target: '8 hrs',
    achieved: false,
    color: 'text-chart-5',
  },
  {
    icon: Activity,
    label: 'Active Minutes',
    value: '45 min',
    target: '60 min',
    achieved: false,
    color: 'text-chart-2',
  },
  {
    icon: Droplets,
    label: 'Hydration',
    value: '1.8 L',
    target: '2.5 L',
    achieved: false,
    color: 'text-chart-3',
  },
];

export function DailySummaryScreen() {
  const navigate = useNavigate();
  
  const todayScore = 87;
  const achievedCount = todayMetrics.filter((m) => m.achieved).length;

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <div className="bg-card border-b border-border px-6 py-4">
        <div className="flex items-center gap-4 mb-4">
          <button
            onClick={() => navigate('/app')}
            className="p-2 -ml-2 hover:bg-muted rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-xl font-semibold text-foreground">Daily Summary</h1>
            <p className="text-sm text-muted-foreground">Monday, March 23</p>
          </div>
        </div>

        {/* Wellness score */}
        <div className="bg-gradient-to-br from-primary to-primary/80 rounded-2xl p-6 text-white">
          <div className="flex items-center justify-between mb-4">
            <div>
              <div className="text-sm opacity-90 mb-1">Wellness Score</div>
              <div className="text-5xl font-bold">{todayScore}</div>
            </div>
            <div className="bg-white/20 rounded-full w-20 h-20 flex items-center justify-center">
              <Calendar className="w-10 h-10" />
            </div>
          </div>
          <div className="text-sm opacity-90">
            {achievedCount} of {todayMetrics.length} targets met
          </div>
        </div>
      </div>

      <div className="p-6 space-y-6">
        {/* Today's metrics */}
        <div>
          <h2 className="text-lg font-semibold mb-4 text-foreground">Today's Activity</h2>
          <div className="space-y-3">
            {todayMetrics.map((metric, index) => {
              const Icon = metric.icon;
              return (
                <div
                  key={index}
                  className="bg-card border border-border rounded-2xl p-4 flex items-center gap-4"
                >
                  <div className={`${metric.color}`}>
                    <Icon className="w-6 h-6" />
                  </div>
                  <div className="flex-1">
                    <div className="text-sm text-muted-foreground">{metric.label}</div>
                    <div className="font-semibold text-foreground">{metric.value}</div>
                  </div>
                  <div className="text-right">
                    <div className="text-xs text-muted-foreground mb-1">Target</div>
                    <div className="text-sm font-medium text-foreground">{metric.target}</div>
                  </div>
                  {metric.achieved ? (
                    <CheckCircle2 className="w-5 h-5 text-success" />
                  ) : (
                    <Circle className="w-5 h-5 text-muted-foreground" />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* Weekly trend */}
        <div>
          <h2 className="text-lg font-semibold mb-4 text-foreground">Weekly Wellness</h2>
          <div className="bg-card border border-border rounded-2xl p-4">
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={weekSummary}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" opacity={0.3} />
                <XAxis
                  dataKey="day"
                  stroke="#9ca3af"
                  fontSize={12}
                  tickLine={false}
                />
                <YAxis stroke="#9ca3af" fontSize={12} tickLine={false} domain={[0, 100]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1a1d23',
                    border: 'none',
                    borderRadius: '12px',
                    color: '#e8eaed',
                  }}
                />
                <Bar dataKey="score" fill="#6366f1" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
            <div className="mt-4 flex items-center justify-between text-sm">
              <div>
                <div className="text-muted-foreground">Weekly average</div>
                <div className="text-xl font-bold text-foreground">87.9</div>
              </div>
              <div className="text-right">
                <div className="text-muted-foreground">Best day</div>
                <div className="text-xl font-bold text-foreground">95</div>
              </div>
            </div>
          </div>
        </div>

        {/* Insights */}
        <div>
          <h2 className="text-lg font-semibold mb-4 text-foreground">Insights</h2>
          <div className="space-y-3">
            <div className="bg-success/10 border border-success/20 rounded-2xl p-4">
              <div className="flex gap-3">
                <CheckCircle2 className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                <div>
                  <div className="font-medium text-foreground mb-1">Great sleep consistency</div>
                  <div className="text-sm text-muted-foreground">
                    You've maintained 7+ hours of sleep for 5 days in a row
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-warning/10 border border-warning/20 rounded-2xl p-4">
              <div className="flex gap-3">
                <Activity className="w-5 h-5 text-warning flex-shrink-0 mt-0.5" />
                <div>
                  <div className="font-medium text-foreground mb-1">Increase activity</div>
                  <div className="text-sm text-muted-foreground">
                    Try adding 15 more active minutes to reach your daily goal
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-info/10 border border-info/20 rounded-2xl p-4">
              <div className="flex gap-3">
                <Droplets className="w-5 h-5 text-info flex-shrink-0 mt-0.5" />
                <div>
                  <div className="font-medium text-foreground mb-1">Stay hydrated</div>
                  <div className="text-sm text-muted-foreground">
                    You're 0.7L away from your daily hydration goal
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}