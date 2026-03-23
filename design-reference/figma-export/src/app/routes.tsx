import { createBrowserRouter } from "react-router";
import { Layout } from "./components/Layout";
import { OnboardingScreen } from "./screens/OnboardingScreen";
import { HomeScreen } from "./screens/HomeScreen";
import { MetricDetailScreen } from "./screens/MetricDetailScreen";
import { ManualEntryScreen } from "./screens/ManualEntryScreen";
import { GoalsScreen } from "./screens/GoalsScreen";
import { DailySummaryScreen } from "./screens/DailySummaryScreen";
import { ProfileScreen } from "./screens/ProfileScreen";
import { HealthConnectScreen } from "./screens/HealthConnectScreen";
import { Navigate } from "react-router";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/onboarding" replace />,
  },
  {
    path: "/onboarding",
    Component: OnboardingScreen,
  },
  {
    path: "/health-connect",
    Component: HealthConnectScreen,
  },
  {
    path: "/app",
    Component: Layout,
    children: [
      { index: true, Component: HomeScreen },
      { path: "metric/:type", Component: MetricDetailScreen },
      { path: "entry", Component: ManualEntryScreen },
      { path: "goals", Component: GoalsScreen },
      { path: "summary", Component: DailySummaryScreen },
      { path: "profile", Component: ProfileScreen },
    ],
  },
]);