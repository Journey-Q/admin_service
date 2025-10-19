"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import {
  Trash2,
  Calendar,
  Star,
  X,
  Check,
  Clock,
  Search,
  TrendingUp,
  Users,
  MapPin,
  Plane,
  Building,
  Package,
  ChevronDown,
  ChevronUp,
  DollarSign,
  Zap,
  CheckCircle,
  XCircle,
  MessageSquare,
  FileText,
  Play,
  Pause,
  Tag,
  RefreshCw,
} from "lucide-react"
import axios from "axios"

const API_BASE_URL = "http://localhost:8080/admin/auth/promotions"

const AdminPromotions = () => {
  const [activeTab, setActiveTab] = useState("pending")
  const [filterServiceType, setFilterServiceType] = useState("all")
  const [searchTerm, setSearchTerm] = useState("")
  const [sortBy, setSortBy] = useState("newest")
  const [selectedPromotions, setSelectedPromotions] = useState([])
  const [showBulkActions, setShowBulkActions] = useState(false)
  const [showPromotionDetails, setShowPromotionDetails] = useState({})
  const [showReviewModal, setShowReviewModal] = useState(false)
  const [reviewingPromotion, setReviewingPromotion] = useState(null)
  const [reviewComment, setReviewComment] = useState("")
  const [promotions, setPromotions] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Get token from localStorage or your auth context
  const getAuthToken = () => {
    return localStorage.getItem("adminToken") || ""
  }

  // Axios instance with auth header
  const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      "Content-Type": "application/json",
    },
  })

  // Add token to every request
  api.interceptors.request.use((config) => {
    const token = getAuthToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  // Service type configurations
  const serviceConfigs = {
    hotel: {
      icon: Building,
      name: "Hotels & Accommodation",
      color: "#0088cc",
    },
    travel: {
      icon: Plane,
      name: "Travel Services",
      color: "#10b981",
    },
    tour: {
      icon: Package,
      name: "Tour Packages",
      color: "#8b5cf6",
    },
  }

  // Fetch promotions based on active tab
  const fetchPromotions = async () => {
    setLoading(true)
    setError(null)
    try {
      let response
      if (activeTab === "all") {
        response = await api.get("/all")
        setPromotions(response.data.promotions || [])
      } else {
        response = await api.get(`/status/${activeTab.toUpperCase()}`)
        setPromotions(response.data.promotions || [])
      }
    } catch (err) {
      console.error("Error fetching promotions:", err)
      setError(err.response?.data?.message || "Failed to fetch promotions")
      setPromotions([])
    } finally {
      setLoading(false)
    }
  }

  // Fetch promotions when tab changes
  useEffect(() => {
    fetchPromotions()
  }, [activeTab])

  // Filter promotions based on current filters
  const filteredPromotions = promotions.filter((promotion) => {
    const matchesServiceType = filterServiceType === "all" || promotion.serviceType === filterServiceType
    const matchesSearch =
      promotion.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      promotion.submittedBy?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      promotion.location?.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesServiceType && matchesSearch
  })

  // Sort promotions
  const sortedPromotions = [...filteredPromotions].sort((a, b) => {
    switch (sortBy) {
      case "newest":
        return new Date(b.submittedDate) - new Date(a.submittedDate)
      case "oldest":
        return new Date(a.submittedDate) - new Date(b.submittedDate)
      case "performance":
        return (b.bookings || 0) - (a.bookings || 0)
      case "revenue":
        return (b.revenue || 0) - (a.revenue || 0)
      case "discount":
        return (b.discount || 0) - (a.discount || 0)
      default:
        return 0
    }
  })

  // Statistics
  const stats = {
    total: promotions.length,
    pending: promotions.filter((p) => p.status?.toLowerCase() === "pending").length,
    approved: promotions.filter((p) => p.status?.toLowerCase() === "approved").length,
    advertised: promotions.filter((p) => p.status?.toLowerCase() === "advertised").length,
    rejected: promotions.filter((p) => p.status?.toLowerCase() === "rejected").length,
    totalRevenue: promotions.reduce((sum, p) => sum + (p.revenue || 0), 0),
    totalBookings: promotions.reduce((sum, p) => sum + (p.bookings || 0), 0),
    avgRating:
      promotions.filter((p) => (p.rating || 0) > 0).length > 0
        ? (
            promotions.filter((p) => (p.rating || 0) > 0).reduce((sum, p) => sum + (p.rating || 0), 0) /
            promotions.filter((p) => (p.rating || 0) > 0).length
          ).toFixed(1)
        : 0,
  }

  // Approve promotion
  const handleApprovePromotion = async (promotionId) => {
    try {
      const response = await api.put(`/${promotionId}/approve`, {
        reviewComment: reviewComment || "Approved by admin",
      })
      console.log("Promotion approved:", response.data)
      await fetchPromotions()
      return true
    } catch (err) {
      console.error("Error approving promotion:", err)
      alert(err.response?.data?.message || "Failed to approve promotion")
      return false
    }
  }

  // Reject promotion
  const handleRejectPromotion = async (promotionId) => {
    try {
      const response = await api.put(`/${promotionId}/reject`, {
        reviewComment: reviewComment || "Rejected by admin",
      })
      console.log("Promotion rejected:", response.data)
      await fetchPromotions()
      return true
    } catch (err) {
      console.error("Error rejecting promotion:", err)
      alert(err.response?.data?.message || "Failed to reject promotion")
      return false
    }
  }

  // Toggle promotion active status
  const handleToggleActive = async (promotionId) => {
    try {
      const response = await api.patch(`/${promotionId}/toggle-active`)
      console.log("Promotion active status toggled:", response.data)
      await fetchPromotions()
    } catch (err) {
      console.error("Error toggling active status:", err)
      alert(err.response?.data?.message || "Failed to toggle active status")
    }
  }

  // Delete promotion
  const handleDeletePromotion = async (promotionId) => {
    if (!window.confirm("Are you sure you want to delete this promotion?")) {
      return
    }

    try {
      await api.delete(`/${promotionId}`)
      console.log("Promotion deleted")
      await fetchPromotions()
    } catch (err) {
      console.error("Error deleting promotion:", err)
      alert(err.response?.data?.message || "Failed to delete promotion")
    }
  }

  // Bulk actions
  const handleBulkAction = async (action) => {
    const selectedIds = selectedPromotions

    try {
      let response
      switch (action) {
        case "approve":
          response = await api.put("/bulk/approve", selectedIds)
          console.log("Bulk approve response:", response.data)
          break
        case "reject":
          response = await api.put("/bulk/reject", selectedIds)
          console.log("Bulk reject response:", response.data)
          break
        case "delete":
          if (window.confirm(`Delete ${selectedIds.length} promotions?`)) {
            response = await api.delete("/bulk/delete", { data: selectedIds })
            console.log("Bulk delete response:", response.data)
          }
          break
      }

      await fetchPromotions()
      setSelectedPromotions([])
      setShowBulkActions(false)

      if (response?.data) {
        alert(
          `${response.data.message}\nSuccessful: ${response.data.successful}\nFailed: ${response.data.failed}`,
        )
      }
    } catch (err) {
      console.error("Error in bulk action:", err)
      alert(err.response?.data?.message || "Failed to perform bulk action")
    }
  }

  // Review modal
  const openReviewModal = (promotion) => {
    setReviewingPromotion(promotion)
    setReviewComment(promotion.reviewComment || "")
    setShowReviewModal(true)
  }

  const handleReviewSubmit = async (action) => {
    if (!reviewingPromotion) return

    let success = false
    if (action === "approved") {
      success = await handleApprovePromotion(reviewingPromotion.id)
    } else if (action === "rejected") {
      success = await handleRejectPromotion(reviewingPromotion.id)
    }

    if (success) {
      setShowReviewModal(false)
      setReviewingPromotion(null)
      setReviewComment("")
    }
  }

  const togglePromotionDetails = (promotionId) => {
    setShowPromotionDetails((prev) => ({
      ...prev,
      [promotionId]: !prev[promotionId],
    }))
  }

  const getStatusBadgeClass = (status) => {
    const statusLower = status?.toLowerCase() || ""
    switch (statusLower) {
      case "approved":
        return "bg-blue-100 text-blue-800"
      case "advertised":
        return "bg-green-100 text-green-800"
      case "pending":
        return "bg-yellow-100 text-yellow-800"
      case "rejected":
        return "bg-red-100 text-red-800"
      default:
        return "bg-gray-100 text-gray-800"
    }
  }

  const getServiceIcon = (type) => {
    const IconComponent = serviceConfigs[type]?.icon || Package
    return <IconComponent className="w-4 h-4" />
  }

  const getServiceColor = (type) => {
    return serviceConfigs[type]?.color || "#6b7280"
  }

  return (
    <div className="p-6 space-y-6 bg-gray-50/50 min-h-screen">
      {/* Header */}
      <Card className="p-6 bg-white shadow-lg border-0">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-[#0088cc]/10 rounded-xl flex items-center justify-center">
              <CheckCircle className="w-6 h-6 text-[#0088cc]" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Admin Promotions Dashboard</h1>
              <p className="text-gray-600">Review and manage promotional campaigns from service providers</p>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <Button
              onClick={fetchPromotions}
              variant="outline"
              className="flex items-center space-x-2"
              disabled={loading}
            >
              <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
              <span>Refresh</span>
            </Button>
            <Badge className="bg-yellow-100 text-yellow-800">{stats.pending} Pending Review</Badge>
          </div>
        </div>
      </Card>

      {/* Error Message */}
      {error && (
        <Card className="p-4 bg-red-50 border border-red-200">
          <div className="flex items-center space-x-2 text-red-800">
            <XCircle className="w-5 h-5" />
            <span>{error}</span>
          </div>
        </Card>
      )}

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-6">
        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-[#0088cc]/10 rounded-xl flex items-center justify-center">
              <Star className="w-6 h-6 text-[#0088cc]" />
            </div>
            <div>
              <p className="text-2xl font-bold">{stats.total}</p>
              <p className="text-sm text-gray-600">Total Promotions</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-yellow-500/10 rounded-xl flex items-center justify-center">
              <Clock className="w-6 h-6 text-yellow-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">{stats.pending}</p>
              <p className="text-sm text-gray-600">Pending</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-blue-500/10 rounded-xl flex items-center justify-center">
              <Check className="w-6 h-6 text-blue-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">{stats.approved}</p>
              <p className="text-sm text-gray-600">Approved</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-green-500/10 rounded-xl flex items-center justify-center">
              <Zap className="w-6 h-6 text-green-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">{stats.advertised}</p>
              <p className="text-sm text-gray-600">Advertised</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-purple-500/10 rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-purple-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">${(stats.totalRevenue / 1000).toFixed(0)}K</p>
              <p className="text-sm text-gray-600">Total Revenue</p>
            </div>
          </div>
        </Card>

        <Card className="p-6 bg-white shadow-lg border-0">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-orange-500/10 rounded-xl flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-orange-500" />
            </div>
            <div>
              <p className="text-2xl font-bold">{stats.totalBookings}</p>
              <p className="text-sm text-gray-600">Total Bookings</p>
            </div>
          </div>
        </Card>
      </div>

      {/* Tabs */}
      <Card className="bg-white shadow-lg border-0">
        <div className="flex border-b overflow-x-auto">
          {[
            { id: "pending", label: "Pending Review", count: stats.pending, color: "text-yellow-600" },
            { id: "approved", label: "Approved", count: stats.approved, color: "text-blue-600" },
            { id: "advertised", label: "Advertised", count: stats.advertised, color: "text-green-600" },
            { id: "rejected", label: "Rejected", count: stats.rejected, color: "text-red-600" },
            { id: "all", label: "All Promotions", count: stats.total, color: "text-gray-600" },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-6 py-4 font-medium transition-colors whitespace-nowrap ${
                activeTab === tab.id
                  ? "text-[#0088cc] border-b-2 border-[#0088cc] bg-[#0088cc]/5"
                  : "text-gray-600 hover:text-gray-800"
              }`}
            >
              <span>{tab.label}</span>
              <Badge
                className={`ml-2 ${activeTab === tab.id ? "bg-[#0088cc] text-white" : "bg-gray-200 text-gray-600"}`}
              >
                {tab.count}
              </Badge>
            </button>
          ))}
        </div>
      </Card>

      {/* Filters */}
      <Card className="p-4 bg-white shadow-lg border-0">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0">
          <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <Input
                type="text"
                placeholder="Search promotions, providers, locations..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 w-full sm:w-80 focus:ring-2 focus:ring-[#0088cc] focus:border-transparent"
              />
            </div>

            <select
              value={filterServiceType}
              onChange={(e) => setFilterServiceType(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#0088cc] focus:border-transparent"
            >
              <option value="all">All Service Types</option>
              {Object.entries(serviceConfigs).map(([key, config]) => (
                <option key={key} value={key}>
                  {config.name}
                </option>
              ))}
            </select>

            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#0088cc] focus:border-transparent"
            >
              <option value="newest">Newest First</option>
              <option value="oldest">Oldest First</option>
              <option value="performance">Best Performance</option>
              <option value="revenue">Highest Revenue</option>
              <option value="discount">Highest Discount</option>
            </select>
          </div>

          {selectedPromotions.length > 0 && (
            <div className="flex items-center space-x-2">
              <span className="text-sm text-gray-600">{selectedPromotions.length} selected</span>
              <Button variant="outline" onClick={() => setShowBulkActions(!showBulkActions)}>
                Bulk Actions
              </Button>
            </div>
          )}
        </div>

        {showBulkActions && (
          <div className="mt-4 p-4 bg-gray-50 rounded-lg flex space-x-2">
            <Button onClick={() => handleBulkAction("approve")} className="bg-[#0088cc] hover:bg-blue-700 text-white">
              Approve All
            </Button>
            <Button onClick={() => handleBulkAction("reject")} className="bg-red-500 hover:bg-red-600 text-white">
              Reject All
            </Button>
            <Button onClick={() => handleBulkAction("delete")} className="bg-gray-500 hover:bg-gray-600 text-white">
              Delete All
            </Button>
          </div>
        )}
      </Card>

      {/* Loading State */}
      {loading && (
        <Card className="p-12 bg-white shadow-lg border-0 text-center">
          <RefreshCw className="w-12 h-12 text-[#0088cc] animate-spin mx-auto mb-4" />
          <p className="text-gray-600">Loading promotions...</p>
        </Card>
      )}

      {/* Promotions Grid */}
      {!loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {sortedPromotions.map((promotion) => (
            <Card
              key={promotion.id}
              className="bg-white shadow-lg border-0 overflow-hidden hover:shadow-xl transition-all duration-300 group"
            >
              {/* Selection Checkbox */}
              <div className="absolute top-4 left-4 z-10">
                <input
                  type="checkbox"
                  checked={selectedPromotions.includes(promotion.id)}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setSelectedPromotions((prev) => [...prev, promotion.id])
                    } else {
                      setSelectedPromotions((prev) => prev.filter((id) => id !== promotion.id))
                    }
                  }}
                  className="w-4 h-4 text-[#0088cc] border-gray-300 rounded focus:ring-[#0088cc]"
                />
              </div>

              {/* Image */}
              <div className="relative h-48">
                <img
                  src={promotion.image || "/api/placeholder/400/250"}
                  alt={promotion.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent" />

                {/* Status Badge */}
                <div className="absolute top-3 right-3">
                  <Badge className={getStatusBadgeClass(promotion.status)}>
                    {promotion.status?.charAt(0).toUpperCase() + promotion.status?.slice(1)}
                  </Badge>
                </div>

                {/* Discount Badge */}
                <div className="absolute top-3 left-12">
                  <Badge className="bg-red-500 text-white">{promotion.discount}% OFF</Badge>
                </div>

                {/* Service Type Badge */}
                <div className="absolute bottom-3 left-3">
                  <Badge
                    className="text-white flex items-center space-x-1"
                    style={{ backgroundColor: getServiceColor(promotion.serviceType) }}
                  >
                    {getServiceIcon(promotion.serviceType)}
                    <span className="capitalize">{promotion.serviceType}</span>
                  </Badge>
                </div>

                {/* Featured Badge */}
                {promotion.featured && (
                  <div className="absolute bottom-3 right-3">
                    <Badge className="bg-yellow-400 text-yellow-900 flex items-center space-x-1">
                      <Star className="w-3 h-3" />
                      <span>Featured</span>
                    </Badge>
                  </div>
                )}
              </div>

              {/* Content */}
              <div className="p-6">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg font-bold text-gray-900 line-clamp-1">{promotion.title}</h3>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => togglePromotionDetails(promotion.id)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    {showPromotionDetails[promotion.id] ? (
                      <ChevronUp className="w-4 h-4" />
                    ) : (
                      <ChevronDown className="w-4 h-4" />
                    )}
                  </Button>
                </div>

                <p className="text-gray-600 text-sm mb-3 line-clamp-2">{promotion.description}</p>

                {/* Provider and Location */}
                <div className="space-y-2 mb-4">
                  <div className="flex items-center space-x-2 text-sm text-gray-500">
                    <Building className="w-4 h-4" />
                    <span className="font-medium">{promotion.submittedBy}</span>
                  </div>
                  <div className="flex items-center space-x-2 text-sm text-gray-500">
                    <MapPin className="w-4 h-4" />
                    <span>{promotion.location}</span>
                  </div>
                  <div className="flex items-center space-x-2 text-sm text-gray-500">
                    <Calendar className="w-4 h-4" />
                    <span>Submitted: {promotion.submittedDate}</span>
                  </div>
                </div>

                {/* Detailed Information (Collapsible) */}
                {showPromotionDetails[promotion.id] && (
                  <div className="mb-4 p-3 bg-gray-50 rounded-lg space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-600">Valid Period:</span>
                      <span className="font-medium">
                        {promotion.validFrom} to {promotion.validTo}
                      </span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-600">Category:</span>
                      <span className="font-medium capitalize">{promotion.category}</span>
                    </div>
                    {promotion.reviewedBy && (
                      <>
                        <div className="flex justify-between text-sm">
                          <span className="text-gray-600">Reviewed by:</span>
                          <span className="font-medium">{promotion.reviewedBy}</span>
                        </div>
                        <div className="flex justify-between text-sm">
                          <span className="text-gray-600">Review Date:</span>
                          <span className="font-medium">{promotion.reviewedDate}</span>
                        </div>
                      </>
                    )}
                    {promotion.reviewComment && (
                      <div className="text-sm">
                        <span className="text-gray-600">Review Comment:</span>
                        <p className="text-gray-800 mt-1">{promotion.reviewComment}</p>
                      </div>
                    )}
                    {promotion.status?.toLowerCase() === "advertised" && (
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div className="text-center">
                          <p className="font-semibold text-gray-900">{promotion.bookings || 0}</p>
                          <p className="text-gray-500">Bookings</p>
                        </div>
                        <div className="text-center">
                          <p className="font-semibold text-gray-900">
                            ${(promotion.revenue || 0).toLocaleString()}
                          </p>
                          <p className="text-gray-500">Revenue</p>
                        </div>
                      </div>
                    )}
                  </div>
                )}

                {/* Actions based on status */}
                <div className="space-y-2">
                  {promotion.status?.toLowerCase() === "pending" && (
                    <div className="flex space-x-2">
                      <Button
                        onClick={() => openReviewModal(promotion)}
                        className="flex-1 bg-[#0088cc] hover:bg-blue-700 text-white flex items-center justify-center space-x-1"
                      >
                        <MessageSquare className="w-4 h-4" />
                        <span>Review</span>
                      </Button>
                      <Button
                        onClick={() => handleApprovePromotion(promotion.id)}
                        className="flex-1 bg-green-500 hover:bg-green-600 text-white flex items-center justify-center space-x-1"
                      >
                        <CheckCircle className="w-4 h-4" />
                        <span>Quick Approve</span>
                      </Button>
                    </div>
                  )}

                  {promotion.status?.toLowerCase() === "approved" && (
                    <div className="w-full bg-blue-50 border border-blue-200 text-blue-800 px-3 py-2 rounded-lg text-center text-sm font-medium">
                      <Check className="w-4 h-4 inline mr-1" />
                      Approved - Awaiting Payment from Provider
                    </div>
                  )}

                  {promotion.status?.toLowerCase() === "advertised" && (
                    <div className="w-full bg-green-50 border border-green-200 text-green-800 px-3 py-2 rounded-lg text-center text-sm font-medium">
                      <Zap className="w-4 h-4 inline mr-1" />
                      Live Campaign - Generating Revenue
                    </div>
                  )}

                  {promotion.status?.toLowerCase() === "rejected" && (
                    <div className="w-full bg-red-50 border border-red-200 text-red-800 px-3 py-2 rounded-lg text-center text-sm font-medium">
                      <XCircle className="w-4 h-4 inline mr-1" />
                      Rejected - Provider Can Resubmit
                    </div>
                  )}

                  <div className="flex space-x-2">
                    <Button
                      variant="outline"
                      onClick={() => openReviewModal(promotion)}
                      className="flex-1 flex items-center justify-center space-x-1"
                    >
                      <FileText className="w-4 h-4" />
                      <span>View Details</span>
                    </Button>

                    {promotion.status?.toLowerCase() === "advertised" && (
                      <Button
                        onClick={() => handleToggleActive(promotion.id)}
                        className={`flex-1 flex items-center justify-center space-x-1 ${
                          promotion.isActive
                            ? "bg-red-100 text-red-700 hover:bg-red-200"
                            : "bg-green-100 text-green-700 hover:bg-green-200"
                        }`}
                      >
                        {promotion.isActive ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4" />}
                        <span>{promotion.isActive ? "Pause" : "Resume"}</span>
                      </Button>
                    )}

                    <Button
                      variant="outline"
                      onClick={() => handleDeletePromotion(promotion.id)}
                      className="bg-red-100 text-red-700 hover:bg-red-200"
                    >
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Empty state */}
      {!loading && sortedPromotions.length === 0 && (
        <Card className="p-12 bg-white shadow-lg border-0 text-center">
          <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Star className="w-8 h-8 text-gray-400" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No promotions found</h3>
          <p className="text-gray-500 mb-4">
            {searchTerm || filterServiceType !== "all"
              ? "Try adjusting your filters or search terms."
              : `No ${activeTab === "all" ? "" : activeTab} promotions available.`}
          </p>
        </Card>
      )}

      {/* Review Modal */}
      {showReviewModal && reviewingPromotion && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-3xl max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-200">
              <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-900">Review Promotion</h2>
                <Button
                  variant="ghost"
                  onClick={() => setShowReviewModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="w-6 h-6" />
                </Button>
              </div>
            </div>

            <div className="p-6 space-y-6">
              {/* Promotion Details */}
              <div className="bg-gray-50 rounded-xl p-4">
                <div className="flex items-start space-x-4">
                  <img
                    src={reviewingPromotion.image || "/api/placeholder/96/96"}
                    alt={reviewingPromotion.title}
                    className="w-24 h-24 object-cover rounded-lg"
                  />
                  <div className="flex-1">
                    <h3 className="text-lg font-bold text-gray-900 mb-1">{reviewingPromotion.title}</h3>
                    <p className="text-gray-600 text-sm mb-2">{reviewingPromotion.description}</p>
                    <div className="flex items-center space-x-4 text-sm text-gray-500">
                      <span className="flex items-center space-x-1">
                        <Building className="w-4 h-4" />
                        <span>{reviewingPromotion.submittedBy}</span>
                      </span>
                      <span className="flex items-center space-x-1">
                        <MapPin className="w-4 h-4" />
                        <span>{reviewingPromotion.location}</span>
                      </span>
                      <span className="flex items-center space-x-1">
                        <Tag className="w-4 h-4" />
                        <span>{reviewingPromotion.discount}% OFF</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Review Details */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-3">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Service Type</label>
                    <p className="text-gray-900 capitalize">{reviewingPromotion.serviceType}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Category</label>
                    <p className="text-gray-900 capitalize">{reviewingPromotion.category}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Submitted Date</label>
                    <p className="text-gray-900">{reviewingPromotion.submittedDate}</p>
                  </div>
                </div>
                <div className="space-y-3">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Valid From</label>
                    <p className="text-gray-900">{reviewingPromotion.validFrom}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Valid To</label>
                    <p className="text-gray-900">{reviewingPromotion.validTo}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Featured Request</label>
                    <p className="text-gray-900">{reviewingPromotion.featured ? "Yes" : "No"}</p>
                  </div>
                </div>
              </div>

              {/* Review Comment */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Review Comment (Optional)</label>
                <textarea
                  value={reviewComment}
                  onChange={(e) => setReviewComment(e.target.value)}
                  rows={4}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#0088cc] focus:border-transparent resize-none"
                  placeholder="Add a comment about your review decision..."
                />
              </div>

              {/* Action Buttons */}
              <div className="flex space-x-4 pt-4 border-t border-gray-200">
                <Button variant="outline" onClick={() => setShowReviewModal(false)} className="flex-1">
                  Cancel
                </Button>
                <Button
                  onClick={() => handleReviewSubmit("rejected")}
                  className="flex-1 bg-red-500 hover:bg-red-600 text-white flex items-center justify-center space-x-2"
                >
                  <XCircle className="w-5 h-5" />
                  <span>Reject</span>
                </Button>
                <Button
                  onClick={() => handleReviewSubmit("approved")}
                  className="flex-1 bg-[#0088cc] hover:bg-blue-700 text-white flex items-center justify-center space-x-2"
                >
                  <CheckCircle className="w-5 h-5" />
                  <span>Approve</span>
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Performance Summary */}
      <Card className="p-6 bg-white shadow-lg border-0">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Performance Summary</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="text-center">
            <div className="w-12 h-12 bg-[#0088cc]/10 rounded-xl mx-auto mb-2 flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-[#0088cc]" />
            </div>
            <p className="text-2xl font-bold text-gray-900">{stats.totalBookings}</p>
            <p className="text-sm text-gray-600">Total Bookings</p>
          </div>
          <div className="text-center">
            <div className="w-12 h-12 bg-green-500/10 rounded-xl mx-auto mb-2 flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-green-500" />
            </div>
            <p className="text-2xl font-bold text-gray-900">${stats.totalRevenue.toLocaleString()}</p>
            <p className="text-sm text-gray-600">Total Revenue</p>
          </div>
          <div className="text-center">
            <div className="w-12 h-12 bg-yellow-500/10 rounded-xl mx-auto mb-2 flex items-center justify-center">
              <Users className="w-6 h-6 text-yellow-500" />
            </div>
            <p className="text-2xl font-bold text-gray-900">{Object.keys(serviceConfigs).length}</p>
            <p className="text-sm text-gray-600">Service Types</p>
          </div>
          <div className="text-center">
            <div className="w-12 h-12 bg-purple-500/10 rounded-xl mx-auto mb-2 flex items-center justify-center">
              <Star className="w-6 h-6 text-purple-500" />
            </div>
            <p className="text-2xl font-bold text-gray-900">{stats.avgRating}</p>
            <p className="text-sm text-gray-600">Avg Rating</p>
          </div>
        </div>
      </Card>
    </div>
  )
}

export default AdminPromotions
