import { useNavigate, useParams } from 'react-router';
import { ArrowLeft, TrendingUp, Calendar, CloudDownload } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';

const metricData = {
  steps: {
    title: 'Steps',
    current: '8,547',
    unit: 'steps',
    goal: 10000,
    color: '#6366f1',
    trend: '+12%',
    synced: true,
    weekData: [
      { day: 'Mon', value: 7234 },
      { day: 'Tue', value: 8912 },
      { day: 'Wed', value: 9456 },
      { day: 'Thu', value: 8123 },
      { day: 'Fri', value: 10234 },
      { day: 'Sat', value: 9876 },
      { day: 'Sun', value: 8547 },
    ],
    monthData: [
      { day: '1', value: 7234 },
      { day: '5', value: 8912 },
      { day: '10', value: 9456 },
      { day: '15', value: 8123 },
      { day: '20', value: 10234 },
      { day: '25', value: 9876 },
      { day: '30', value: 8547 },
    ],
  },
  heart: {
    title: 'Heart Rate',
    current: '72',
    unit: 'bpm',
    color: '#ec4899',
    trend: '-3%',
    synced: true,
    weekData: [
      { day: 'Mon', value: 74 },
      { day: 'Tue', value: 73 },
      { day: 'Wed', value: 75 },
      { day: 'Thu', value: 71 },
      { day: 'Fri', value: 72 },
      { day: 'Sat', value: 70 },
      { day: 'Sun', value: 72 },
    ],
    monthData: [
      { day: '1', value: 74 },
      { day: '5', value: 73 },
      { day: '10', value: 75 },
      { day: '15', value: 71 },
      { day: '20', value: 72 },
      { day: '25', value: 70 },
      { day: '30', value: 72 },
    ],
  },
  sleep: {
    title: 'Sleep',
    current: '7.5',
    unit: 'hours',
    goal: 8,
    color: '#8b5cf6',
    trend: '+8%',
    synced: true,
    weekData: [
      { day: 'Mon', value: 7.2 },
      { day: 'Tue', value: 6.8 },
      { day: 'Wed', value: 7.5 },
      { day: 'Thu', value: 8.1 },
      { day: 'Fri', value: 7.0 },
      { day: 'Sat', value: 8.5 },
      { day: 'Sun', value: 7.5 },
    ],
    monthData: [
      { day: '1', value: 7.2 },
      { day: '5', value: 6.8 },
      { day: '10', value: 7.5 },
      { day: '15', value: 8.1 },
      { day: '20', value: 7.0 },
      { day: '25', value: 8.5 },
      { day: '30', value: 7.5 },
    ],
  },
  activity: {
    title: 'Active Minutes',
    current: '45',
    unit: 'min',
    goal: 60,
    color: '#10b981',
    trend: '+15%',
    synced: true,
    weekData: [
      { day: 'Mon', value: 35 },
      { day: 'Tue', value: 42 },
      { day: 'Wed', value: 50 },
      { day: 'Thu', value: 38 },
      { day: 'Fri', value: 55 },
      { day: 'Sat', value: 60 },
      { day: 'Sun', value: 45 },
    ],
    monthData: [
      { day: '1', value: 35 },
      { day: '5', value: 42 },
      { day: '10', value: 50 },
      { day: '15', value: 38 },
      { day: '20', value: 55 },
      { day: '25', value: 60 },
      { day: '30', value: 45 },
    ],
  },
  hydration: {
    title: 'Hydration',
    current: '1.8',
    unit: 'liters',
    goal: 2.5,
    color: '#f59e0b',
    synced: false,
    weekData: [
      { day: 'Mon', value: 2.1 },
      { day: 'Tue', value: 1.9 },
      { day: 'Wed', value: 2.3 },
      { day: 'Thu', value: 1.7 },
      { day: 'Fri', value: 2.0 },
      { day: 'Sat', value: 2.4 },
      { day: 'Sun', value: 1.8 },
    ],
    monthData: [
      { day: '1', value: 2.1 },
      { day: '5', value: 1.9 },
      { day: '10', value: 2.3 },
      { day: '15', value: 1.7 },
      { day: '20', value: 2.0 },
      { day: '25', value: 2.4 },
      { day: '30', value: 1.8 },
    ],
  },
};

export function MetricDetailScreen() {
  const navigate = useNavigate();
  const { type } = useParams<{ type: string }>();
  const metric = metricData[type as keyof typeof metricData];

  if (!metric) {
    return <div>Metric not found</div>;
  }

  const goalPercentage = metric.goal
    ? Math.round((parseFloat(metric.current) / metric.goal) * 100)
    : 0;

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <div className="bg-card border-b border-border px-6 py-4">
        <div className="flex items-center gap-4 mb-4">
          <button
            onClick={() => navigate('/app')}
            className="p-2 -ml-2 hover:bg-muted rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-semibold text-foreground">{metric.title}</h1>
        </div>

        {/* Current value card */}
        <div className="bg-muted/50 rounded-2xl p-5">
          <div className="flex items-start justify-between mb-3">
            <div>
              <div className="text-sm text-muted-foreground mb-2">Current</div>
              <div className="flex items-baseline gap-2">
                <span className="text-4xl font-bold" style={{ color: metric.color }}>
                  {metric.current}
                </span>
                <span className="text-lg text-muted-foreground">{metric.unit}</span>
              </div>
            </div>
            {metric.synced && (
              <div className="flex items-center gap-1 text-xs text-muted-foreground bg-card px-2 py-1 rounded-lg">
                <CloudDownload className="w-3 h-3" />
                <span>Synced</span>
              </div>
            )}
          </div>

          {metric.trend && (
            <div className="flex items-center gap-2 mb-3">
              <TrendingUp className="w-4 h-4 text-success" />
              <span className="text-sm text-success">{metric.trend} vs last week</span>
            </div>
          )}

          {metric.goal && (
            <div>
              <div className="flex items-center justify-between text-sm mb-2">
                <span className="text-muted-foreground">Daily goal</span>
                <span className="font-medium text-foreground">
                  {goalPercentage}% complete
                </span>
              </div>
              <div className="bg-card rounded-full h-2 overflow-hidden">
                <div
                  className="h-full rounded-full transition-all"
                  style={{
                    width: `${Math.min(goalPercentage, 100)}%`,
                    backgroundColor: metric.color,
                  }}
                />
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Chart section */}
      <div className="p-6">
        <Tabs defaultValue="week" className="w-full">
          <TabsList className="grid w-full grid-cols-2 mb-6">
            <TabsTrigger value="week">Week</TabsTrigger>
            <TabsTrigger value="month">Month</TabsTrigger>
          </TabsList>

          <TabsContent value="week" className="space-y-4">
            <div className="bg-card border border-border rounded-2xl p-4">
              <h3 className="text-sm font-medium mb-4 text-foreground">Last 7 days</h3>
              <ResponsiveContainer width="100%" height={220}>
                <AreaChart data={metric.weekData}>
                  <defs>
                    <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor={metric.color} stopOpacity={0.3} />
                      <stop offset="95%" stopColor={metric.color} stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" opacity={0.3} />
                  <XAxis
                    dataKey="day"
                    stroke="#9ca3af"
                    fontSize={12}
                    tickLine={false}
                  />
                  <YAxis stroke="#9ca3af" fontSize={12} tickLine={false} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#1a1d23',
                      border: 'none',
                      borderRadius: '12px',
                      color: '#e8eaed',
                    }}
                  />
                  <Area
                    type="monotone"
                    dataKey="value"
                    stroke={metric.color}
                    strokeWidth={2}
                    fill="url(#colorValue)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="bg-card border border-border rounded-2xl p-4">
                <div className="text-xs text-muted-foreground mb-1">Average</div>
                <div className="text-2xl font-bold text-foreground">
                  {(
                    metric.weekData.reduce((sum, d) => sum + d.value, 0) /
                    metric.weekData.length
                  ).toFixed(1)}
                </div>
                <div className="text-xs text-muted-foreground">{metric.unit}</div>
              </div>
              <div className="bg-card border border-border rounded-2xl p-4">
                <div className="text-xs text-muted-foreground mb-1">Best day</div>
                <div className="text-2xl font-bold text-foreground">
                  {Math.max(...metric.weekData.map((d) => d.value)).toFixed(1)}
                </div>
                <div className="text-xs text-muted-foreground">{metric.unit}</div>
              </div>
            </div>
          </TabsContent>

          <TabsContent value="month" className="space-y-4">
            <div className="bg-card border border-border rounded-2xl p-4">
              <h3 className="text-sm font-medium mb-4 text-foreground">Last 30 days</h3>
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={metric.monthData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" opacity={0.3} />
                  <XAxis
                    dataKey="day"
                    stroke="#9ca3af"
                    fontSize={12}
                    tickLine={false}
                  />
                  <YAxis stroke="#9ca3af" fontSize={12} tickLine={false} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#1a1d23',
                      border: 'none',
                      borderRadius: '12px',
                      color: '#e8eaed',
                    }}
                  />
                  <Line
                    type="monotone"
                    dataKey="value"
                    stroke={metric.color}
                    strokeWidth={3}
                    dot={{ fill: metric.color, r: 4 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}