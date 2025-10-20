"use client"

import type React from "react"
import { useState, useEffect } from "react"
import {
  DollarSign,
  Calendar,
  Users,
  Download,
  Search,
  Building2,
  Settings,
  BarChart3,
  Save,
  Plus,
  Minus,
  ChevronDown,
  ChevronRight,
  CreditCard,
  MapPin,
  Phone,
  Globe,
  Eye,
  Percent,
  RefreshCw,
} from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Progress } from "@/components/ui/progress"

// ==================== API Configuration ====================
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// Helper function to get auth token
const getAuthToken = () => {
  return localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
};

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = getAuthToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
  };
};

// Helper functions to map provider types
const mapProviderTypeToBackend = (type) => {
  const mapping = {
    'Hotel': 'HOTEL',
    'Travel Service': 'TRAVEL_SERVICE',
    'Tour Service': 'TOUR_SERVICE',
  };
  return mapping[type] || type;
};

const mapProviderTypeFromBackend = (type) => {
  const mapping = {
    'HOTEL': 'Hotel',
    'TRAVEL_SERVICE': 'Travel Service',
    'TOUR_SERVICE': 'Tour Service',
  };
  return mapping[type] || type;
};

// ==================== API Functions ====================
const commissionAPI = {
  // Get all commission settings
  getSettings: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/admin/auth/commissions/settings`, {
        method: 'GET',
        headers: getAuthHeaders(),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching settings:', error);
      throw error;
    }
  },

  // Update commission settings (bulk update)
  updateSettings: async (settings) => {
    try {
      // Convert settings object to array format expected by backend
      const settingsArray = Object.entries(settings).map(([type, rate]) => ({
        providerType: mapProviderTypeToBackend(type),
        commissionRate: parseFloat(rate)
      }));

      console.log('Sending settings update:', settingsArray);

      const response = await fetch(`${API_BASE_URL}/admin/auth/commissions/settings/bulk`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(settingsArray),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error updating settings:', error);
      throw error;
    }
  },

  // Get all commissions
  getAllCommissions: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/admin/auth/commissions`, {
        method: 'GET',
        headers: getAuthHeaders(),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching commissions:', error);
      throw error;
    }
  },

  // Get commission statistics
  getStatistics: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/admin/auth/commissions/statistics`, {
        method: 'GET',
        headers: getAuthHeaders(),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching statistics:', error);
      throw error;
    }
  },

  // Initialize default settings
  initializeSettings: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/admin/auth/commissions/settings/initialize`, {
        method: 'POST',
        headers: getAuthHeaders(),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error initializing settings:', error);
      throw error;
    }
  },
};

// ==================== TypeScript Interfaces ====================
interface CommissionData {
  id: string
  serviceProvider: string
  providerType: "Hotel" | "Travel Service" | "Tour Service"
  amount: number
  percentage: number
  date: string
  transactionId: string
  revenue: number
  businessRegNo: string
  paymentMethod: string
  customerCount: number
  month: string
  location: string
  phone: string
  website: string
  address: string
}

interface CommissionSettings {
  Hotel: number
  "Travel Service": number
  "Tour Service": number
}

// ==================== Main Component ====================
const Commissions: React.FC = () => {
  const [activeTab, setActiveTab] = useState("settings")
  const [searchTerm, setSearchTerm] = useState("")
  const [filterType, setFilterType] = useState("all")
  const [selectedMonth, setSelectedMonth] = useState("all")
  const [selectedProvider, setSelectedProvider] = useState<CommissionData | null>(null)
  const [expandedProviders, setExpandedProviders] = useState<string[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  // Commission settings state
  const [commissionSettings, setCommissionSettings] = useState<CommissionSettings>({
    Hotel: 12.5,
    "Travel Service": 10.0,
    "Tour Service": 15.0,
  })
  const [tempSettings, setTempSettings] = useState<CommissionSettings>(commissionSettings)

  // Commission data state
  const [commissions, setCommissions] = useState<CommissionData[]>([])
  const [stats, setStats] = useState({
    totalCommissions: 0,
    totalRevenue: 0,
  })

  // Load data on component mount
  useEffect(() => {
    loadInitialData()
  }, [])

  const loadInitialData = async () => {
    setLoading(true)
    setError(null)
    try {
      // Load commission settings
      await loadCommissionSettings()

      // Load commissions data
      await loadCommissions()

      // Load statistics
      await loadStatistics()
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load data'
      setError(errorMessage)
      console.error('Error loading initial data:', err)
    } finally {
      setLoading(false)
    }
  }

  const loadCommissionSettings = async () => {
    try {
      const response = await commissionAPI.getSettings()
      console.log('Loaded settings response:', response)

      if (response.settings) {
        const mappedSettings = {
          Hotel: parseFloat(response.settings.HOTEL || 12.5),
          "Travel Service": parseFloat(response.settings.TRAVEL_SERVICE || 10.0),
          "Tour Service": parseFloat(response.settings.TOUR_SERVICE || 15.0),
        }
        console.log('Mapped settings:', mappedSettings)
        setCommissionSettings(mappedSettings)
        setTempSettings(mappedSettings)
      }
    } catch (err) {
      console.error('Error loading commission settings:', err)
      // Try to initialize if settings don't exist
      try {
        console.log('Attempting to initialize default settings...')
        await commissionAPI.initializeSettings()
        await loadCommissionSettings()
      } catch (initErr) {
        console.error('Error initializing settings:', initErr)
        // Use default settings if initialization fails
        setCommissionSettings({
          Hotel: 12.5,
          "Travel Service": 10.0,
          "Tour Service": 15.0,
        })
        setTempSettings({
          Hotel: 12.5,
          "Travel Service": 10.0,
          "Tour Service": 15.0,
        })
      }
    }
  }

  const loadCommissions = async () => {
    try {
      const response = await commissionAPI.getAllCommissions()
      console.log('Loaded commissions response:', response)

      if (response.commissions && Array.isArray(response.commissions)) {
        // Map backend data to frontend format
        const mappedCommissions = response.commissions.map((c: any) => ({
          id: c.id.toString(),
          serviceProvider: c.serviceProvider,
          providerType: mapProviderTypeFromBackend(c.providerType),
          amount: parseFloat(c.amount),
          percentage: parseFloat(c.percentage),
          date: c.date,
          transactionId: c.transactionId,
          revenue: parseFloat(c.revenue),
          businessRegNo: c.businessRegNo || '',
          paymentMethod: c.paymentMethod || '',
          customerCount: c.customerCount || 0,
          month: c.month || '',
          location: c.location || '',
          phone: c.phone || '',
          website: c.website || '',
          address: c.address || '',
        }))
        setCommissions(mappedCommissions)
      } else {
        setCommissions([])
      }
    } catch (err) {
      console.error('Error loading commissions:', err)
      // Use empty array if no commissions exist yet
      setCommissions([])
    }
  }

  const loadStatistics = async () => {
    try {
      const response = await commissionAPI.getStatistics()
      console.log('Loaded statistics response:', response)

      setStats({
        totalCommissions: parseFloat(response.totalCommissions || 0),
        totalRevenue: parseFloat(response.totalRevenue || 0),
      })
    } catch (err) {
      console.error('Error loading statistics:', err)
      // Calculate from loaded commissions if API fails
      const totalCommissions = commissions.reduce((sum, c) => sum + c.amount, 0)
      const totalRevenue = commissions.reduce((sum, c) => sum + c.revenue, 0)
      setStats({ totalCommissions, totalRevenue })
    }
  }

  const months = [...new Set(commissions.map(c => c.month).filter(Boolean))]
  const uniqueProviders = [...new Set(commissions.map((c) => c.serviceProvider))]

  const handleSettingsChange = (type: keyof CommissionSettings, value: number) => {
    // Ensure value is within bounds (0-25%)
    const newValue = Math.max(0, Math.min(25, value))
    setTempSettings((prev) => ({
      ...prev,
      [type]: parseFloat(newValue.toFixed(1)),
    }))
  }

  const saveSettings = async () => {
    setLoading(true)
    setError(null)
    setSuccessMessage(null)

    try {
      console.log('Saving settings:', tempSettings)
      const response = await commissionAPI.updateSettings(tempSettings)
      console.log('Save response:', response)

      // Update the current settings to match temp settings
      setCommissionSettings(tempSettings)
      setSuccessMessage('Commission settings updated successfully!')

      // Clear success message after 3 seconds
      setTimeout(() => setSuccessMessage(null), 3000)

      // Reload data to ensure consistency
      await loadCommissionSettings()
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to save settings'
      setError(errorMessage)
      console.error('Error saving settings:', err)
    } finally {
      setLoading(false)
    }
  }

  const resetSettings = () => {
    setTempSettings(commissionSettings)
    setError(null)
  }

  const refreshData = async () => {
    await loadInitialData()
  }

  const toggleProviderExpansion = (provider: string) => {
    setExpandedProviders((prev) =>
      prev.includes(provider)
        ? prev.filter((p) => p !== provider)
        : [...prev, provider]
    )
  }

  const handleViewProvider = (commission: CommissionData) => {
    setSelectedProvider(commission)
  }

  const handleCloseModal = () => {
    setSelectedProvider(null)
  }

  const getFilteredCommissions = () => {
    return commissions.filter((commission) => {
      const matchesSearch =
        commission.serviceProvider.toLowerCase().includes(searchTerm.toLowerCase()) ||
        commission.transactionId.toLowerCase().includes(searchTerm.toLowerCase()) ||
        commission.businessRegNo.toLowerCase().includes(searchTerm.toLowerCase())
      const matchesType = filterType === "all" || commission.providerType.toLowerCase() === filterType.toLowerCase()
      const matchesMonth = selectedMonth === "all" || commission.month === selectedMonth
      return matchesSearch && matchesType && matchesMonth
    })
  }

  const filteredCommissions = getFilteredCommissions()

  const getTypeBadgeClass = (type: string) => {
    switch (type) {
      case "Hotel":
        return "bg-blue-100 text-blue-800"
      case "Tour Service":
        return "bg-green-100 text-green-800"
      case "Travel Service":
        return "bg-purple-100 text-purple-800"
      default:
        return "bg-gray-100 text-gray-800"
    }
  }

  const getTypeIcon = (type: string) => {
    switch (type) {
      case "Hotel":
        return <Building2 className="w-4 h-4" />
      case "Tour Service":
        return <Users className="w-4 h-4" />
      case "Travel Service":
        return <CreditCard className="w-4 h-4" />
      default:
        return <Building2 className="w-4 h-4" />
    }
  }

  const getCommissionsByType = (type: string) => {
    return commissions.filter((c) => c.providerType === type).length
  }

  const getTotalCommissionByType = (type: string) => {
    return commissions.filter((c) => c.providerType === type).reduce((sum, c) => sum + c.amount, 0)
  }

  const getPercentageColor = (percentage: number) => {
    if (percentage >= 15) return "text-green-600 bg-green-50"
    if (percentage >= 12) return "text-blue-600 bg-blue-50"
    if (percentage >= 10) return "text-orange-600 bg-orange-50"
    return "text-red-600 bg-red-50"
  }

  // Check if settings have changed
  const hasChanges = JSON.stringify(tempSettings) !== JSON.stringify(commissionSettings)

  return (
    <div className="p-6 space-y-6 bg-gray-50/50 min-h-screen">
      {/* Error and Success Messages */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex justify-between items-center">
          <span>{error}</span>
          <button onClick={() => setError(null)} className="text-red-900 font-bold">×</button>
        </div>
      )}
      {successMessage && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg flex justify-between items-center">
          <span>{successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-green-900 font-bold">×</button>
        </div>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-indigo-500/10 rounded-xl flex items-center justify-center">
              <Percent className="w-6 h-6 text-indigo-500" />
            </div>
            <div className="space-y-2 flex-1">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium text-gray-900">Commission Rates</p>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={refreshData}
                  disabled={loading}
                  className="h-6 w-6 p-0"
                >
                  <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                </Button>
              </div>
              <div className="space-y-1">
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-600 flex items-center gap-1">
                    <Building2 className="w-3 h-3" />
                    Hotel
                  </span>
                  <span
                    className={`text-xs font-bold px-2 py-1 rounded ${getPercentageColor(commissionSettings.Hotel)}`}
                  >
                    {commissionSettings.Hotel}%
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-600 flex items-center gap-1">
                    <Users className="w-3 h-3" />
                    Tour
                  </span>
                  <span
                    className={`text-xs font-bold px-2 py-1 rounded ${getPercentageColor(commissionSettings["Tour Service"])}`}
                  >
                    {commissionSettings["Tour Service"]}%
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-600 flex items-center gap-1">
                    <CreditCard className="w-3 h-3" />
                    Travel
                  </span>
                  <span
                    className={`text-xs font-bold px-2 py-1 rounded ${getPercentageColor(commissionSettings["Travel Service"])}`}
                  >
                    {commissionSettings["Travel Service"]}%
                  </span>
                </div>
              </div>
            </div>
          </div>
        </Card>
        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-green-500/10 rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-green-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">Rs {stats.totalCommissions.toLocaleString()}</p>
              <p className="text-sm text-gray-600">Total Commissions</p>
            </div>
          </div>
        </Card>
        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-purple-500/10 rounded-xl flex items-center justify-center">
              <BarChart3 className="w-6 h-6 text-purple-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">Rs {stats.totalRevenue.toLocaleString()}</p>
              <p className="text-sm text-gray-600">Total Revenue</p>
            </div>
          </div>
        </Card>
      </div>

      {/* Tabs */}
      <Card className="bg-white shadow-lg border-0">
        <div className="flex border-b">
          <button
            onClick={() => setActiveTab("settings")}
            className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors ${
              activeTab === "settings"
                ? "text-[#0088cc] border-b-2 border-[#0088cc] bg-[#0088cc]/5"
                : "text-gray-600 hover:text-gray-800"
            }`}
          >
            <Settings className="w-5 h-5" />
            Commission Settings
            {hasChanges && <Badge className="bg-yellow-100 text-yellow-800 ml-2">Unsaved</Badge>}
          </button>
          <button
            onClick={() => setActiveTab("overview")}
            className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors ${
              activeTab === "overview"
                ? "text-[#0088cc] border-b-2 border-[#0088cc] bg-[#0088cc]/5"
                : "text-gray-600 hover:text-gray-800"
            }`}
          >
            <BarChart3 className="w-5 h-5" />
            Commission Overview
            <Badge className="bg-blue-100 text-blue-800 ml-2">{commissions.length}</Badge>
          </button>
          <button
            onClick={() => setActiveTab("providers")}
            className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors ${
              activeTab === "providers"
                ? "text-[#0088cc] border-b-2 border-[#0088cc] bg-[#0088cc]/5"
                : "text-gray-600 hover:text-gray-800"
            }`}
          >
            <Building2 className="w-5 h-5" />
            Provider Details
            <Badge className="bg-green-100 text-green-800 ml-2">{uniqueProviders.length}</Badge>
          </button>
          <button
            onClick={() => setActiveTab("monthly")}
            className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors ${
              activeTab === "monthly"
                ? "text-[#0088cc] border-b-2 border-[#0088cc] bg-[#0088cc]/5"
                : "text-gray-600 hover:text-gray-800"
            }`}
          >
            <Calendar className="w-5 h-5" />
            Monthly Reports
          </button>
        </div>
      </Card>

      {/* Commission Settings Tab */}
      {activeTab === "settings" && (
        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="mb-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Commission Rate Settings</h3>
            <p className="text-gray-600">Configure commission percentages for different service provider types</p>
          </div>
          <div className="space-y-6">
            {Object.entries(tempSettings).map(([type, rate]) => (
              <div key={type} className="flex items-center justify-between p-6 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-4">
                  <div className="w-12 h-12 bg-[#0088cc]/10 rounded-xl flex items-center justify-center">
                    {getTypeIcon(type)}
                  </div>
                  <div>
                    <h3 className="font-medium text-gray-900">{type}</h3>
                    <p className="text-sm text-gray-500">
                      {getCommissionsByType(type)} providers • Rs {getTotalCommissionByType(type).toLocaleString()}{" "}
                      total
                    </p>
                  </div>
                </div>
                <div className="flex items-center space-x-4">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleSettingsChange(type as keyof CommissionSettings, rate - 0.5)}
                    disabled={loading || rate <= 0}
                  >
                    <Minus className="w-4 h-4" />
                  </Button>
                  <div className="text-center min-w-[100px]">
                    <div className={`text-3xl font-bold px-4 py-2 rounded-lg ${getPercentageColor(rate)}`}>
                      {rate.toFixed(1)}%
                    </div>
                    <div className="text-xs text-gray-500 mt-1">Commission Rate</div>
                    <div className="w-20 mt-2">
                      <Progress value={(rate / 25) * 100} className="h-2" />
                    </div>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleSettingsChange(type as keyof CommissionSettings, rate + 0.5)}
                    disabled={loading || rate >= 25}
                  >
                    <Plus className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            ))}
            <div className="flex justify-end space-x-3 pt-4 border-t">
              <Button
                variant="outline"
                onClick={resetSettings}
                disabled={loading || !hasChanges}
              >
                Reset Changes
              </Button>
              <Button
                onClick={saveSettings}
                className="bg-[#0088cc] hover:bg-[#0088cc]/90"
                disabled={loading || !hasChanges}
              >
                <Save className="w-4 h-4 mr-2" />
                {loading ? 'Saving...' : 'Save Settings'}
              </Button>
            </div>
          </div>
        </Card>
      )}

      {/* Overview Tab */}
      {activeTab === "overview" && (
        <>
          {/* Filters */}
          <Card className="p-4 bg-white shadow-lg border-0">
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <Input
                  type="text"
                  placeholder="Search by provider, transaction ID..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
              <select
                value={filterType}
                onChange={(e) => setFilterType(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#0088cc] focus:border-transparent w-[180px]"
              >
                <option value="all">All Types</option>
                <option value="hotel">Hotel</option>
                <option value="tour service">Tour Service</option>
                <option value="travel service">Travel Service</option>
              </select>
              <select
                value={selectedMonth}
                onChange={(e) => setSelectedMonth(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#0088cc] focus:border-transparent w-[180px]"
              >
                <option value="all">All Months</option>
                {months.map((month) => (
                  <option key={month} value={month}>
                    {new Date(month + "-01").toLocaleDateString("en-US", { year: "numeric", month: "long" })}
                  </option>
                ))}
              </select>
            </div>
          </Card>

          {/* Commission Table */}
          <Card className="p-6 bg-white shadow-lg border-0">
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Commission Transactions</h3>
              <p className="text-gray-600">Track all commission payments from service providers</p>
            </div>
            {loading ? (
              <div className="text-center py-8">
                <RefreshCw className="w-8 h-8 animate-spin mx-auto text-gray-400" />
                <p className="text-gray-500 mt-2">Loading commissions...</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b">
                      <th className="text-left p-3 font-medium text-gray-900">Provider</th>
                      <th className="text-left p-3 font-medium text-gray-900">Type</th>
                      <th className="text-left p-3 font-medium text-gray-900">Commission</th>
                      <th className="text-left p-3 font-medium text-gray-900">Revenue</th>
                      <th className="text-left p-3 font-medium text-gray-900">Commission Rate</th>
                      <th className="text-left p-3 font-medium text-gray-900">Date</th>
                      <th className="text-left p-3 font-medium text-gray-900">Transaction ID</th>
                      <th className="text-left p-3 font-medium text-gray-900">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredCommissions.length === 0 ? (
                      <tr>
                        <td colSpan={8} className="p-8 text-center text-gray-500">
                          No commission records found
                        </td>
                      </tr>
                    ) : (
                      filteredCommissions.map((commission) => (
                        <tr key={commission.id} className="border-b hover:bg-gray-50">
                          <td className="p-3">
                            <div>
                              <p className="font-medium">{commission.serviceProvider}</p>
                              <p className="text-sm text-gray-500">{commission.location}</p>
                            </div>
                          </td>
                          <td className="p-3">
                            <Badge className={getTypeBadgeClass(commission.providerType)}>
                              {getTypeIcon(commission.providerType)}
                              <span className="ml-1">{commission.providerType}</span>
                            </Badge>
                          </td>
                          <td className="p-3">
                            <span className="font-semibold text-green-600">Rs {commission.amount.toLocaleString()}</span>
                          </td>
                          <td className="p-3">
                            <span className="font-medium">Rs {commission.revenue.toLocaleString()}</span>
                          </td>
                          <td className="p-3">
                            <div className="flex items-center gap-2">
                              <div
                                className={`px-3 py-1 rounded-full text-sm font-bold ${getPercentageColor(commission.percentage)}`}
                              >
                                {commission.percentage}%
                              </div>
                              <div className="w-16">
                                <Progress value={(commission.percentage / 20) * 100} className="h-2" />
                              </div>
                            </div>
                          </td>
                          <td className="p-3 text-gray-600">{new Date(commission.date).toLocaleDateString()}</td>
                          <td className="p-3">
                            <span className="font-mono text-sm text-gray-700">{commission.transactionId}</span>
                          </td>
                          <td className="p-3">
                            <Button size="sm" variant="outline" onClick={() => handleViewProvider(commission)}>
                              <Eye className="w-4 h-4 mr-1" />
                              View
                            </Button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </Card>
        </>
      )}

      {/* Provider Details Tab - Keeping it brief for length */}
      {activeTab === "providers" && (
        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="mb-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Provider Commission Details</h3>
            <p className="text-gray-600">Detailed commission breakdown by service provider</p>
          </div>
          {uniqueProviders.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              No providers found
            </div>
          ) : (
            <div className="space-y-4">
              {uniqueProviders.map((provider) => {
                const providerCommissions = commissions.filter((c) => c.serviceProvider === provider)
                const totalCommission = providerCommissions.reduce((sum, c) => sum + c.amount, 0)
                const averageRate =
                  providerCommissions.reduce((sum, c) => sum + c.percentage, 0) / providerCommissions.length
                const isExpanded = expandedProviders.includes(provider)
                return (
                  <div key={provider} className="border rounded-lg">
                    <button
                      onClick={() => toggleProviderExpansion(provider)}
                      className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors"
                    >
                      <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 bg-[#0088cc]/10 rounded-xl flex items-center justify-center">
                          {getTypeIcon(providerCommissions[0].providerType)}
                        </div>
                        <div className="text-left">
                          <div className="font-medium text-gray-900">{provider}</div>
                          <div className="text-sm text-gray-500">
                            {providerCommissions[0].providerType} • {providerCommissions.length} transactions
                          </div>
                        </div>
                      </div>
                      <div className="flex items-center space-x-4">
                        <div className="text-right">
                          <div className="font-semibold text-green-600">Rs {totalCommission.toLocaleString()}</div>
                          <div className="text-sm text-gray-500">Total Commission</div>
                        </div>
                        <div className={`px-3 py-1 rounded-full text-sm font-bold ${getPercentageColor(averageRate)}`}>
                          {averageRate.toFixed(1)}%
                        </div>
                        {isExpanded ? <ChevronDown className="w-5 h-5" /> : <ChevronRight className="w-5 h-5" />}
                      </div>
                    </button>
                    {isExpanded && (
                      <div className="border-t bg-gray-50 p-4">
                        <div className="space-y-2">
                          {providerCommissions.map((commission) => (
                            <div
                              key={commission.id}
                              className="flex items-center justify-between p-3 bg-white border rounded-lg"
                            >
                              <div className="flex items-center space-x-3">
                                <span className="text-sm text-gray-600">{commission.transactionId}</span>
                                <span className="text-sm text-gray-500">
                                  {new Date(commission.date).toLocaleDateString()}
                                </span>
                              </div>
                              <div className="flex items-center space-x-3">
                                <div className="text-right">
                                  <div className="font-medium text-green-600">
                                    Rs {commission.amount.toLocaleString()}
                                  </div>
                                  <div className="text-xs text-gray-500">Commission</div>
                                </div>
                                <div
                                  className={`px-2 py-1 rounded text-xs font-bold ${getPercentageColor(commission.percentage)}`}
                                >
                                  {commission.percentage}%
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                )
              })}
            </div>
          )}
        </Card>
      )}

      {/* Monthly Reports Tab */}
      {activeTab === "monthly" && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {months.length === 0 ? (
            <div className="col-span-2 text-center py-8 text-gray-500">
              No monthly data available
            </div>
          ) : (
            months.map((month) => {
              const monthCommissions = commissions.filter((c) => c.month === month)
              const monthTotal = monthCommissions.reduce((sum, c) => sum + c.amount, 0)
              const monthRevenue = monthCommissions.reduce((sum, c) => sum + c.revenue, 0)
              const avgRate =
                monthCommissions.length > 0
                  ? monthCommissions.reduce((sum, c) => sum + c.percentage, 0) / monthCommissions.length
                  : 0
              return (
                <Card key={month} className="p-6 bg-white shadow-lg border-0">
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-[#0088cc]/10 rounded-xl flex items-center justify-center">
                        <Calendar className="w-5 h-5 text-[#0088cc]" />
                      </div>
                      <h3 className="text-lg font-semibold">
                        {new Date(month + "-01").toLocaleDateString("en-US", { year: "numeric", month: "long" })}
                      </h3>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge className="bg-[#0088cc]/10 text-[#0088cc]">{monthCommissions.length} transactions</Badge>
                      <div className={`px-2 py-1 rounded text-sm font-bold ${getPercentageColor(avgRate)}`}>
                        {avgRate.toFixed(1)}% avg
                      </div>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-4 mb-4">
                    <div className="text-center p-3 bg-green-50 rounded-lg">
                      <div className="text-2xl font-bold text-green-600">Rs {monthTotal.toLocaleString()}</div>
                      <div className="text-sm text-green-600">Total Commission</div>
                    </div>
                    <div className="text-center p-3 bg-blue-50 rounded-lg">
                      <div className="text-2xl font-bold text-blue-600">Rs {monthRevenue.toLocaleString()}</div>
                      <div className="text-sm text-blue-600">Total Revenue</div>
                    </div>
                  </div>
                  <div className="space-y-2">
                    {["Hotel", "Travel Service", "Tour Service"].map((type) => {
                      const typeCommissions = monthCommissions.filter((c) => c.providerType === type)
                      const typeTotal = typeCommissions.reduce((sum, c) => sum + c.amount, 0)
                      const typeAvgRate =
                        typeCommissions.length > 0
                          ? typeCommissions.reduce((sum, c) => sum + c.percentage, 0) / typeCommissions.length
                          : 0
                      return (
                        <div key={type} className="flex items-center justify-between p-3 bg-gray-50 rounded">
                          <div className="flex items-center space-x-2">
                            {getTypeIcon(type)}
                            <span className="text-sm font-medium">{type}</span>
                          </div>
                          <div className="flex items-center space-x-3">
                            <div className="text-sm font-semibold">Rs {typeTotal.toLocaleString()}</div>
                            {typeCommissions.length > 0 && (
                              <div className={`px-2 py-1 rounded text-xs font-bold ${getPercentageColor(typeAvgRate)}`}>
                                {typeAvgRate.toFixed(1)}%
                              </div>
                            )}
                          </div>
                        </div>
                      )
                    })}
                  </div>
                </Card>
              )
            })
          )}
        </div>
      )}

      {/* Provider Details Modal */}
      {selectedProvider && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto m-4">
            <div className="p-6">
              {/* Modal Header */}
              <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 bg-[#0088cc]/10 rounded-xl flex items-center justify-center">
                    {getTypeIcon(selectedProvider.providerType)}
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold text-gray-900">{selectedProvider.serviceProvider}</h2>
                    <p className="text-lg text-gray-600 font-medium">{selectedProvider.providerType}</p>
                    <p className="text-sm text-gray-500">{selectedProvider.businessRegNo}</p>
                  </div>
                </div>
                <button onClick={handleCloseModal} className="text-gray-400 hover:text-gray-600 text-2xl font-bold">
                  ×
                </button>
              </div>
              {/* Provider Details Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Commission Information */}
                <div className="space-y-4">
                  <h3 className="text-lg font-semibold text-gray-900 border-b pb-2">Commission Details</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Commission Amount:</span>
                      <span className="font-semibold text-green-600">
                        Rs {selectedProvider.amount.toLocaleString()}
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-600">Commission Rate:</span>
                      <div
                        className={`px-3 py-1 rounded-full font-bold ${getPercentageColor(selectedProvider.percentage)}`}
                      >
                        {selectedProvider.percentage}%
                      </div>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Revenue Generated:</span>
                      <span className="font-medium">Rs {selectedProvider.revenue.toLocaleString()}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Transaction ID:</span>
                      <span className="font-mono text-sm">{selectedProvider.transactionId}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Payment Method:</span>
                      <span className="font-medium">{selectedProvider.paymentMethod}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Customer Count:</span>
                      <span className="font-medium">{selectedProvider.customerCount}</span>
                    </div>
                  </div>
                </div>
                {/* Contact & Location */}
                <div className="space-y-4">
                  <h3 className="text-lg font-semibold text-gray-900 border-b pb-2">Contact & Location</h3>
                  <div className="space-y-3">
                    <div>
                      <span className="text-gray-600 text-sm">Address:</span>
                      <p className="text-gray-800 mt-1 leading-relaxed">{selectedProvider.address}</p>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Location:</span>
                      <span className="font-medium flex items-center gap-1">
                        <MapPin className="w-4 h-4" />
                        {selectedProvider.location}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Phone:</span>
                      <span className="font-medium flex items-center gap-1">
                        <Phone className="w-4 h-4" />
                        {selectedProvider.phone}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Website:</span>
                      <span className="font-medium flex items-center gap-1">
                        <Globe className="w-4 h-4" />
                        {selectedProvider.website}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Transaction Date:</span>
                      <span className="font-medium">{new Date(selectedProvider.date).toLocaleDateString()}</span>
                    </div>
                  </div>
                </div>
              </div>
              {/* Action Buttons */}
              <div className="flex gap-3 mt-8 pt-6 border-t">
                <Button onClick={handleCloseModal} className="bg-[#0088cc] hover:bg-[#0088cc]/90">
                  Close
                </Button>
                <Button variant="outline">
                  <Download className="w-4 h-4 mr-2" />
                  Export Details
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Commissions
