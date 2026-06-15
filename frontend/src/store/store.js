import { configureStore } from '@reduxjs/toolkit';

export const store = configureStore({
  reducer: {
    app: (state = { status: 'idle' }, action) => state
  },
});
