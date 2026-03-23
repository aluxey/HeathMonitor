import { useState } from 'react';
import { Footprints, Heart, Moon, Activity, Plus, Trophy, TrendingUp } from 'lucide-react';
import { GoalCard } from '../components/GoalCard';
import { Button } from '../components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../components/ui/dialog';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';

const goals = [
  {
    id: 'steps',
    icon: <Footprints className="w-6 h-6" />,
    title: 'Daily Steps',
    current: 8547,
    target: 10000,
    unit: 'steps',
    color: 'bg-chart-1',
  },
  {
    id: 'sleep',
    icon: <Moon className="w-6 h-6" />,
    title: 'Sleep Duration',
    current: 7.5,
    target: 8,
    unit: 'hours',
    color: 'bg-chart-5',
  },
  {
    id: 'activity',
    icon: <Activity className="w-6 h-6" />,
    title: 'Active Minutes',
    current: 45,
    target: 60,
    unit: 'minutes',
    color: 'bg-chart-2',
  },
  {
    id: 'heart',
    icon: <Heart className="w-6 h-6" />,
    title: 'Resting Heart Rate',
    current: 72,
    target: 65,
    unit: 'bpm',
    color: 'bg-chart-4',
  },
];

export function GoalsScreen() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  
  const achievedGoals = goals.filter((g) => g.current >= g.target).length;
  const totalGoals = goals.length;
  const achievementRate = Math.round((achievedGoals / totalGoals) * 100);

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <div className="bg-card border-b border-border px-6 py-6">
        <h1 className="text-2xl font-bold mb-1 text-foreground">Goals & Progress</h1>
        <p className="text-sm text-muted-foreground">Track your wellness targets</p>
      </div>

      {/* Summary card */}
      <div className="p-6">
        <div className="bg-gradient-to-br from-primary to-primary/80 rounded-2xl p-6 text-white mb-6">
          <div className="flex items-start justify-between mb-4">
            <div>
              <div className="text-sm opacity-90 mb-1">Achievement rate</div>
              <div className="text-4xl font-bold">{achievementRate}%</div>
            </div>
            <div className="bg-white/20 rounded-xl p-3">
              <Trophy className="w-8 h-8" />
            </div>
          </div>
          <div className="flex items-center gap-2 text-sm">
            <TrendingUp className="w-4 h-4" />
            <span>
              {achievedGoals} of {totalGoals} goals achieved today
            </span>
          </div>
        </div>

        {/* Goals list */}
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-foreground">Your Goals</h2>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button size="sm" variant="ghost" className="text-primary">
                <Plus className="w-4 h-4 mr-1" />
                Add goal
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-sm">
              <DialogHeader>
                <DialogTitle>Add New Goal</DialogTitle>
              </DialogHeader>
              <div className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="goal-type">Goal type</Label>
                  <select
                    id="goal-type"
                    className="w-full h-10 rounded-lg border border-border bg-card px-3 text-sm"
                  >
                    <option>Daily Steps</option>
                    <option>Sleep Duration</option>
                    <option>Active Minutes</option>
                    <option>Water Intake</option>
                  </select>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="target">Target value</Label>
                  <Input
                    id="target"
                    type="number"
                    placeholder="10000"
                    className="rounded-lg"
                  />
                </div>
                <Button
                  onClick={() => setIsDialogOpen(false)}
                  className="w-full rounded-lg"
                >
                  Add Goal
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </div>

        <div className="space-y-4">
          {goals.map((goal) => (
            <GoalCard key={goal.id} {...goal} />
          ))}
        </div>

        {/* Tips section */}
        <div className="mt-6 bg-muted/30 rounded-2xl p-5">
          <h3 className="font-semibold mb-3 text-foreground flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-primary" />
            Tips to reach your goals
          </h3>
          <ul className="space-y-2 text-sm text-muted-foreground">
            <li className="flex gap-2">
              <span className="text-primary">•</span>
              <span>Set realistic targets that challenge you without overwhelming</span>
            </li>
            <li className="flex gap-2">
              <span className="text-primary">•</span>
              <span>Review and adjust your goals weekly based on progress</span>
            </li>
            <li className="flex gap-2">
              <span className="text-primary">•</span>
              <span>Celebrate small wins to maintain motivation</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}
