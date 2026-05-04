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
    
    // Mock Data
    let mockPenalties = Array.from({ length: 35 }).map((_, i) => ({
        id: i + 1,
        title: `Sample Regulatory Penalty #${i + 1}`,
        description: `This is a detailed description for regulatory penalty #${i + 1}. It involves violations of specific compliance protocols and requires immediate attention from the regulatory team.`,
        severity: ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'][Math.floor(Math.random() * 4)],
        status: ['ACTIVE', 'PENDING', 'RESOLVED', 'CLOSED'][Math.floor(Math.random() * 4)]
    })).reverse(); // Reverse to show newest first

    let filteredPenalties = [...mockPenalties];

    const updateStats = () => {
        statTotal.innerText = mockPenalties.length;
        statActive.innerText = mockPenalties.filter(p => p.status === 'ACTIVE').length;
        statResolved.innerText = mockPenalties.filter(p => p.status === 'RESOLVED').length;
        statPending.innerText = mockPenalties.filter(p => p.status === 'PENDING').length;
    };

    const applyFilters = () => {
        const searchTerm = searchInput.value.toLowerCase();
        const severity = severityFilter.value;
        const status = statusFilter.value;

        filteredPenalties = mockPenalties.filter(p => {
            const matchesSearch = p.title.toLowerCase().includes(searchTerm) || 
                                  p.description.toLowerCase().includes(searchTerm) || 
                                  p.id.toString().includes(searchTerm);
            const matchesSeverity = severity === "" || p.severity === severity;
            const matchesStatus = status === "" || p.status === status;
            return matchesSearch && matchesSeverity && matchesStatus;
        });

        currentPage = 0; // reset to first page when filtering
        fetchPenalties();
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
    createPenaltyForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const penalty = {
            id: mockPenalties.length > 0 ? Math.max(...mockPenalties.map(p => p.id)) + 1 : 1,
            title: document.getElementById('penaltyTitle').value,
            description: document.getElementById('penaltyDescription').value,
            severity: document.getElementById('penaltySeverity').value,
            status: document.getElementById('penaltyStatus').value
        };

        mockPenalties.unshift(penalty); // Add to beginning
        applyFilters(); // Re-apply filters and render
        updateStats();

        createModal.style.display = 'none';
        createPenaltyForm.reset();
        
        // Show success visual feedback (optional)
        const btn = document.getElementById('savePenaltyBtn');
        const origText = btn.innerText;
        btn.innerText = 'Saved!';
        setTimeout(() => btn.innerText = origText, 2000);
    });

    // Pagination controls
    document.getElementById('btnPrev').addEventListener('click', () => {
        if (currentPage > 0) {
            currentPage--;
            fetchPenalties();
        }
    });

    document.getElementById('btnNext').addEventListener('click', () => {
        const totalPages = Math.ceil(filteredPenalties.length / itemsPerPage);
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchPenalties();
        }
    });

    // Fetch penalties (Mocked)
    const fetchPenalties = () => {
        tableBody.style.display = 'none';
        loadingIndicator.style.display = 'flex';
        
        // Simulate network delay for realistic feel
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
        }, 300);
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
                const penalty = mockPenalties.find(p => p.id === id);
                
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

    updateStats(); // Initial stats calculation
    fetchPenalties(); // Start the app by fetching initial data
});
