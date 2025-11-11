import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  state: () => ({
    collapsed: false,
    loading: false,
    breadcrumb: [] as string[]
  }),
  actions: {
    toggleCollapsed() {
      this.collapsed = !this.collapsed;
    },
    setLoading(value: boolean) {
      this.loading = value;
    },
    setBreadcrumb(items: string[]) {
      this.breadcrumb = items;
    }
  }
});
