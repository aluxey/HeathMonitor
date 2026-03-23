import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Heart, Activity, Target, Shield, ChevronRight } from 'lucide-react';
import { Button } from '../components/ui/button';

const onboardingSteps = [
  {
    icon: Heart,
    title: 'Welcome to HealthMonitor',
    description: 'Your personal wellness companion for tracking health metrics and achieving your wellness goals.',
    color: 'bg-primary',
  },
  {
    icon: Activity,
    title: 'Track Your Health',
    description: 'Monitor steps, heart rate, sleep, and other vital metrics in one simple dashboard.',
    color: 'bg-chart-2',
  },
  {
    icon: Target,
    title: 'Set Your Goals',
    description: 'Define personalized health goals and track your progress with visual insights.',
    color: 'bg-chart-3',
  },
  {
    icon: Shield,
    title: 'Local & Private',
    description: 'All your health data stays on your device. No cloud sync, no sharing, completely private.',
    color: 'bg-chart-4',
  },
];

export function OnboardingScreen() {
  const [currentStep, setCurrentStep] = useState(0);
  const navigate = useNavigate();

  const handleNext = () => {
    if (currentStep < onboardingSteps.length - 1) {
      setCurrentStep(currentStep + 1);
    } else {
      navigate('/health-connect');
    }
  };

  const handleSkip = () => {
    navigate('/health-connect');
  };

  const step = onboardingSteps[currentStep];
  const Icon = step.icon;

  return (
    <div className="min-h-screen flex flex-col bg-background">
      {/* Skip button */}
      <div className="p-6 flex justify-end">
        <button
          onClick={handleSkip}
          className="text-sm text-muted-foreground hover:text-foreground transition-colors"
        >
          Skip
        </button>
      </div>

      {/* Content */}
      <div className="flex-1 flex flex-col items-center justify-center px-8 pb-12">
        <div className={`${step.color} rounded-3xl p-8 mb-8 text-white`}>
          <Icon className="w-16 h-16" strokeWidth={1.5} />
        </div>

        <h1 className="text-3xl font-bold text-center mb-4 text-foreground">
          {step.title}
        </h1>
        <p className="text-center text-muted-foreground text-lg leading-relaxed max-w-sm">
          {step.description}
        </p>
      </div>

      {/* Bottom section */}
      <div className="p-6 space-y-4">
        {/* Progress dots */}
        <div className="flex justify-center gap-2 mb-6">
          {onboardingSteps.map((_, index) => (
            <div
              key={index}
              className={`h-2 rounded-full transition-all ${
                index === currentStep
                  ? 'w-8 bg-primary'
                  : 'w-2 bg-muted'
              }`}
            />
          ))}
        </div>

        <Button
          onClick={handleNext}
          className="w-full h-14 text-base rounded-xl"
        >
          {currentStep === onboardingSteps.length - 1 ? 'Get Started' : 'Continue'}
          <ChevronRight className="w-5 h-5 ml-2" />
        </Button>
      </div>
    </div>
  );
}
