import { defineStore } from 'pinia';
import { ref } from 'vue';
import { Report, ModerationSettings } from '../types/report-moderation';
import ReportService from '../services/report-service';

export const useReportStore = defineStore('report', () => {
  const service = ReportService.getInstance();
  const myReports = ref<Report[]>([]);
  const allReports = ref<Report[]>([]);
  const statistics = ref<Record<string, number>>({});
  const isLoading = ref(false);

  async function submitReport(request: Parameters<typeof service.submitReport>[0]) {
    isLoading.value = true;
    try {
      return await service.submitReport(request);
    } finally {
      isLoading.value = false;
    }
  }

  async function loadMyReports() {
    isLoading.value = true;
    try {
      myReports.value = await service.getMyReports();
    } finally {
      isLoading.value = false;
    }
  }

  async function loadReports(status?: string) {
    isLoading.value = true;
    try {
      allReports.value = await service.getReports(status);
    } finally {
      isLoading.value = false;
    }
  }

  async function reviewReport(reportId: string, status: string, reviewNote: string) {
    await service.reviewReport(reportId, status, reviewNote);
    await loadReports();
  }

  async function loadStatistics() {
    statistics.value = await service.getStatistics();
  }

  return { myReports, allReports, statistics, isLoading, submitReport, loadMyReports, loadReports, reviewReport, loadStatistics };
});
