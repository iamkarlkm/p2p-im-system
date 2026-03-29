// routes/do-not-disturb-routes.tsx
import { DoNotDisturbPage } from '../pages/DoNotDisturbPage';

export const doNotDisturbRoutes = [
  {
    path: '/settings/do-not-disturb',
    element: <DoNotDisturbPage />,
    title: '免打扰设置',
  },
];
