import { useState } from 'react';
import { useNavigate } from 'react-router';
import { ArrowLeft, Droplets, Weight, Ruler, Thermometer, CheckCircle2 } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';

const entryTypes = [
  {
    id: 'hydration',
    icon: Droplets,
    label: 'Water Intake',
    unit: 'liters',
    color: 'bg-chart-3',
    placeholder: '2.5',
  },
  {
    id: 'weight',
    icon: Weight,
    label: 'Weight',
    unit: 'kg',
    color: 'bg-chart-1',
    placeholder: '70',
  },
  {
    id: 'height',
    icon: Ruler,
    label: 'Height',
    unit: 'cm',
    color: 'bg-chart-2',
    placeholder: '175',
  },
  {
    id: 'temperature',
    icon: Thermometer,
    label: 'Temperature',
    unit: '°C',
    color: 'bg-chart-4',
    placeholder: '36.5',
  },
];

export function ManualEntryScreen() {
  const navigate = useNavigate();
  const [selectedType, setSelectedType] = useState<string | null>(null);
  const [value, setValue] = useState('');
  const [showSuccess, setShowSuccess] = useState(false);

  const handleSave = () => {
    if (!selectedType || !value) return;
    
    setShowSuccess(true);
    setTimeout(() => {
      navigate('/app');
    }, 1500);
  };

  const selectedEntry = entryTypes.find((t) => t.id === selectedType);

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <div className="bg-card border-b border-border px-6 py-4">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/app')}
            className="p-2 -ml-2 hover:bg-muted rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-semibold text-foreground">Manual Entry</h1>
        </div>
      </div>

      <div className="p-6 space-y-6">
        {/* Success message */}
        {showSuccess && (
          <div className="bg-success/10 border border-success/20 rounded-2xl p-4 flex items-center gap-3">
            <CheckCircle2 className="w-5 h-5 text-success" />
            <span className="text-success font-medium">Entry saved successfully!</span>
          </div>
        )}

        {/* Select metric type */}
        <div>
          <h2 className="text-sm font-semibold mb-3 text-foreground">Select metric</h2>
          <div className="grid grid-cols-2 gap-3">
            {entryTypes.map((type) => {
              const Icon = type.icon;
              const isSelected = selectedType === type.id;

              return (
                <button
                  key={type.id}
                  onClick={() => setSelectedType(type.id)}
                  className={`bg-card border rounded-2xl p-4 flex flex-col items-center gap-3 transition-all ${
                    isSelected
                      ? 'border-primary shadow-md scale-[1.02]'
                      : 'border-border hover:shadow-sm'
                  }`}
                >
                  <div className={`${type.color} rounded-xl p-3 text-white`}>
                    <Icon className="w-6 h-6" />
                  </div>
                  <div className="text-center">
                    <div className="text-sm font-medium text-foreground">{type.label}</div>
                    <div className="text-xs text-muted-foreground mt-0.5">{type.unit}</div>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Value input */}
        {selectedEntry && (
          <div className="space-y-4">
            <div>
              <label className="text-sm font-semibold mb-2 block text-foreground">
                Enter value
              </label>
              <div className="relative">
                <Input
                  type="number"
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                  placeholder={selectedEntry.placeholder}
                  className="text-2xl h-16 pr-16 rounded-xl"
                />
                <div className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground text-lg">
                  {selectedEntry.unit}
                </div>
              </div>
            </div>

            {/* Date/time info */}
            <div className="bg-muted/50 rounded-xl p-4">
              <div className="text-xs text-muted-foreground mb-1">Recording time</div>
              <div className="text-sm font-medium text-foreground">
                {new Date().toLocaleString('en-US', {
                  weekday: 'long',
                  month: 'long',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </div>
            </div>

            {/* Quick presets for hydration */}
            {selectedType === 'hydration' && (
              <div>
                <div className="text-sm font-semibold mb-2 text-foreground">Quick add</div>
                <div className="grid grid-cols-4 gap-2">
                  {['0.25', '0.5', '1.0', '1.5'].map((preset) => (
                    <button
                      key={preset}
                      onClick={() => setValue(preset)}
                      className="bg-card border border-border rounded-xl py-2 px-3 text-sm font-medium hover:border-primary hover:text-primary transition-colors"
                    >
                      {preset}L
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Bottom action */}
      {selectedEntry && (
        <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto p-6 bg-background border-t border-border">
          <Button
            onClick={handleSave}
            disabled={!value || showSuccess}
            className="w-full h-14 text-base rounded-xl"
          >
            Save Entry
          </Button>
        </div>
      )}
    </div>
  );
}