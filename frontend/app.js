document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('penaltyTableBody');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const createModal = document.getElementById('createModal');
    const createPenaltyForm = document.getElementById('createPenaltyForm');
    
    // View Modal Elements
    const viewModal = document.getElementById('viewModal');
    const viewPenaltyContent = document.getElementById('viewPenaltyContent');
    const closeViewModalBtn = document.getElementById('closeViewModalBtn');
    const closeViewModalBottomBtn = document.getElementById('closeViewModalBottomBtn');
    const pageIndicator = document.getElementById('pageIndicator');

    // Filter Elements
    const searchInput = document.getElementById('searchInput');
    const severityFilter = document.getElementById('severityFilter');
    const statusFilter = document.getElementById('statusFilter');

    // Stats Elements
    const statTotal = document.getElementById('statTotal');
    const statActive = document.getElementById('statActive');
    const statResolved = document.getElementById('statResolved');
    const statPending = document.getElementById('statPending');

    // Navigation and Sections
    const navItems = document.querySelectorAll('#sidebarNav .nav-item');
    const dashboardStats = document.getElementById('dashboardStats');
    const dataView = document.getElementById('dataView');
    const analyticsSection = document.getElementById('analyticsSection');
    const settingsSection = document.getElementById('settingsSection');
    
    // Navigation Logic
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Remove active class from all
            navItems.forEach(nav => nav.classList.remove('active'));
            // Add active class to clicked
            item.classList.add('active');
            
            // Hide all sections
            dashboardStats.style.display = 'none';
            dataView.style.display = 'none';
            analyticsSection.style.display = 'none';
            settingsSection.style.display = 'none';
            
            // Show target section(s) based on data-tab
            const tab = item.getAttribute('data-tab');
            if (tab === 'dashboard') {
                dashboardStats.style.display = 'grid'; // Grid is default for dashboard-stats in style.css
                dataView.style.display = 'block';
            } else if (tab === 'penalties') {
                dataView.style.display = 'block';
            } else if (tab === 'analytics') {
                analyticsSection.style.display = 'block';
            } else if (tab === 'settings') {
                settingsSection.style.display = 'block';
            }
        });
    });
    
    // Pagination State
    let currentPage = 0;
    const itemsPerPage = 10;
    
    // Data State
    let allPenalties = [];
    let filteredPenalties = [];
    let authToken = '';
    const API_BASE = 'http://localhost:8080/api';

    const loginAndFetchData = async () => {
        try {
            // 1. Authenticate as admin
            const authRes = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: 'admin', password: 'admin' })
            });
            if (authRes.ok) {
                const authData = await authRes.json();
                authToken = authData.token;
                
                // 2. Fetch all penalties (using a large size to get all for local filtering/stats)
                const dataRes = await fetch(`${API_BASE}/penalties/all?size=1000&sort=createdAt,desc`, {
                    headers: { 'Authorization': `Bearer ${authToken}` }
                });
                
                if (dataRes.ok) {
                    const data = await dataRes.json();
                    allPenalties = data.content || [];
                    applyFilters();
                    updateStats();
                }
            } else {
                console.error('Authentication failed');
            }
        } catch (error) {
            console.error('Error connecting to backend:', error);
        }
    };

    const updateStats = () => {
        statTotal.innerText = allPenalties.length;
        statActive.innerText = allPenalties.filter(p => p.status === 'ACTIVE').length;
        statResolved.innerText = allPenalties.filter(p => p.status === 'RESOLVED').length;
        statPending.innerText = allPenalties.filter(p => p.status === 'PENDING').length;
    };

    const applyFilters = () => {
        const searchTerm = searchInput.value.toLowerCase();
        const severity = severityFilter.value;
        const status = statusFilter.value;

        filteredPenalties = allPenalties.filter(p => {
            const matchesSearch = p.title.toLowerCase().includes(searchTerm) || 
                                  p.description.toLowerCase().includes(searchTerm) || 
                                  p.id.toString().includes(searchTerm);
            const matchesSeverity = severity === "" || p.severity === severity;
            const matchesStatus = status === "" || p.status === status;
            return matchesSearch && matchesSeverity && matchesStatus;
        });

        currentPage = 0; // reset to first page when filtering
        renderCurrentPage();
    };

    searchInput.addEventListener('input', applyFilters);
    severityFilter.addEventListener('change', applyFilters);
    statusFilter.addEventListener('change', applyFilters);

    // Modal controls for Create
    document.getElementById('btnCreatePenalty').addEventListener('click', () => {
        createModal.style.display = 'flex';
    });
    
    document.getElementById('closeModalBtn').addEventListener('click', () => {
        createModal.style.display = 'none';
    });
    
    document.getElementById('cancelModalBtn').addEventListener('click', () => {
        createModal.style.display = 'none';
    });

    // Modal controls for View
    closeViewModalBtn.addEventListener('click', () => {
        viewModal.style.display = 'none';
    });

    closeViewModalBottomBtn.addEventListener('click', () => {
        viewModal.style.display = 'none';
    });

    // Create Penalty Form Submit
    createPenaltyForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const btn = document.getElementById('savePenaltyBtn');
        const origText = btn.innerText;
        btn.innerText = 'Saving...';
        btn.disabled = true;

        const penaltyData = {
            title: document.getElementById('penaltyTitle').value,
            description: document.getElementById('penaltyDescription').value,
            severity: document.getElementById('penaltySeverity').value,
            status: document.getElementById('penaltyStatus').value
        };

        try {
            const res = await fetch(`${API_BASE}/penalties/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(penaltyData)
            });

            if (res.ok) {
                const newPenalty = await res.json();
                allPenalties.unshift(newPenalty); // Add to beginning
                applyFilters(); // Re-apply filters and render
                updateStats();

                createModal.style.display = 'none';
                createPenaltyForm.reset();
                
                btn.innerText = 'Saved!';
            } else {
                alert('Failed to save penalty to database');
                btn.innerText = origText;
            }
        } catch (error) {
            console.error('Error saving penalty:', error);
            alert('Failed to save penalty. Is backend running?');
            btn.innerText = origText;
        } finally {
            setTimeout(() => {
                btn.innerText = origText;
                btn.disabled = false;
            }, 2000);
        }
    });

    // Pagination controls
    document.getElementById('btnPrev').addEventListener('click', () => {
        if (currentPage > 0) {
            currentPage--;
            renderCurrentPage();
        }
    });

    document.getElementById('btnNext').addEventListener('click', () => {
        const totalPages = Math.ceil(filteredPenalties.length / itemsPerPage);
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderCurrentPage();
        }
    });

    // Render current page data
    const renderCurrentPage = () => {
        tableBody.style.display = 'none';
        loadingIndicator.style.display = 'flex';
        
        setTimeout(() => { 
            const totalPages = Math.ceil(filteredPenalties.length / itemsPerPage) || 1;
            const start = currentPage * itemsPerPage;
            const currentPenalties = filteredPenalties.slice(start, start + itemsPerPage);
            
            pageIndicator.innerText = `Page ${currentPage + 1} of ${totalPages}`;
            renderTable(currentPenalties);
            
            // Update button states
            document.getElementById('btnPrev').style.opacity = currentPage === 0 ? '0.5' : '1';
            document.getElementById('btnNext').style.opacity = currentPage >= totalPages - 1 ? '0.5' : '1';
            
            loadingIndicator.style.display = 'none';
            tableBody.style.display = 'table-row-group';
        }, 100);
    };

    const renderTable = (data) => {
        tableBody.innerHTML = '';
        if (data.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="5" style="text-align: center; padding: 2rem;">No penalties match your search criteria</td>`;
            tableBody.appendChild(tr);
            return;
        }

        data.forEach((item) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>#${item.id}</td>
                <td><strong>${item.title}</strong></td>
                <td><span class="badge ${item.severity.toLowerCase()}">${item.severity}</span></td>
                <td><span class="badge ${item.status === 'ACTIVE' || item.status === 'PENDING' ? 'warning' : 'success'}">${item.status}</span></td>
                <td>
                    <button class="btn glass-btn action-btn" data-id="${item.id}" style="padding: 6px 12px; font-size: 0.8rem;">View Case</button>
                </td>
            `;
            tableBody.appendChild(tr);
        });

        // Add event listeners to newly created "View Case" buttons
        document.querySelectorAll('.action-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = parseInt(e.target.getAttribute('data-id'));
                const penalty = allPenalties.find(p => p.id === id);
                
                if (penalty) {
                    // Populate View Modal
                    viewPenaltyContent.innerHTML = `
                        <p><strong>ID:</strong> #${penalty.id}</p>
                        <p><strong>Title:</strong> ${penalty.title}</p>
                        <p><strong>Severity:</strong> <span class="badge ${penalty.severity.toLowerCase()}">${penalty.severity}</span></p>
                        <p><strong>Status:</strong> <span class="badge ${penalty.status === 'ACTIVE' || penalty.status === 'PENDING' ? 'warning' : 'success'}">${penalty.status}</span></p>
                        <hr style="border: 0; height: 1px; background: var(--border-glass); margin: 8px 0;">
                        <p><strong>Description:</strong></p>
                        <p style="background: rgba(15, 23, 42, 0.6); padding: 12px; border-radius: 8px; font-size: 0.95rem; line-height: 1.5;">${penalty.description || 'No description provided.'}</p>
                    `;
                    viewModal.style.display = 'flex';
                }
            });
        });
    };

    loginAndFetchData(); // Start the app by authenticating and fetching data
});
