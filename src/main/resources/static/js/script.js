// src/main/resources/static/js/script.js

// --- Variables Globales y Selectores DOM ---
const API_BASE_URL = 'https://localhost:8080/api';

// Secciones principales
const authSection = document.getElementById('auth-section');
const appSection = document.getElementById('app-section');

// Formularios de autenticación
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const showLoginBtn = document.getElementById('show-login-btn');
const showRegisterBtn = document.getElementById('show-register-btn');
const loginMessage = document.getElementById('login-message');
const registerMessage = document.getElementById('register-message');

// Elementos de la aplicación principal
const plantListDiv = document.getElementById('plant-list');
const addPlantBtn = document.getElementById('add-plant-btn');
const logoutBtn = document.getElementById('logout-btn');
const notificationsSection = document.getElementById('notifications-section');
const notificationListDiv = document.getElementById('notification-list');

// Modal y formulario de planta
const plantFormModal = document.getElementById('plant-form-modal');
const plantForm = document.getElementById('plant-form');
const plantFormTitle = document.getElementById('plant-form-title');
const cancelPlantFormBtn = document.getElementById('cancel-plant-form-btn');
const plantIdInput = document.getElementById('plant-id');
const plantUserIdInput = document.getElementById('plant-user-id');
const fotoInput = document.getElementById('foto');

// Modal y formulario de riego
const waterPlantModal = document.getElementById('water-plant-modal');
const waterPlantForm = document.getElementById('water-plant-form');
const waterPlantNameSpan = document.getElementById('water-plant-name');
const waterPlantIdInput = document.getElementById('water-plant-id');
const cancelWaterPlantBtn = document.getElementById('cancel-water-plant-btn');

// --- Estado de la aplicación ---
let currentUserId = null;
let currentUserName = null;
let authorizationHeader = null; // Variable para guardar el header de autorización

// --- Funciones de Utilidad ---

// Muestra un mensaje en el DOM (éxito o error)
function showMessage(element, message, isSuccess = true) {
    element.textContent = message;
    element.className = isSuccess ? 'message-success text-center text-sm mt-4' : 'message-error text-center text-sm mt-4';
}

// Oculta/muestra secciones de la UI
function showAuthSection() {
    authSection.classList.remove('hidden');
    appSection.classList.add('hidden');
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
    authorizationHeader = null; // Limpiar autorización al volver a la sección de auth
    currentUserId = null;
    currentUserName = null;
}

function showAppSection() {
    authSection.classList.add('hidden');
    appSection.classList.remove('hidden');
    loadUserPlants(currentUserId);
    loadUserNotifications(currentUserId);
}

// Limpia los formularios
function clearForm(form) {
    form.reset();
    const messageElement = form.querySelector('p[id$="-message"]');
    if (messageElement) {
        messageElement.textContent = '';
        messageElement.className = 'text-center text-sm mt-4';
    }
}

// --- Funciones de Interacción con la API (Fetch) ---

// Autenticar Usuario
async function authenticateUser(email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const userInfo = await response.json();
            currentUserId = userInfo.idUsuario;
            currentUserName = userInfo.nombre;

            // Guardar el header para futuras peticiones
            const credentialsString = `${email}:${password}`;
            authorizationHeader = 'Basic ' + btoa(credentialsString);

            showMessage(loginMessage, `¡Bienvenido, ${currentUserName}!`, true);
            setTimeout(showAppSection, 1000);
        } else {
            const errorText = await response.text();
            let errorDetails = `Error al iniciar sesión. Detalles: ${errorText || response.statusText}`;
            showMessage(loginMessage, errorDetails, false);
            console.error('Error al iniciar sesión:', response.status, errorDetails);
        }
    } catch (error) {
        console.error('Error de conexión al servidor al iniciar sesión:', error);
        showMessage(loginMessage, 'Error de conexión al servidor.', false);
    }
}

// Registrar Usuario
async function registerUser(name, email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/registrar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: name, email, password })
        });

        if (response.ok) {
            showMessage(registerMessage, `Usuario registrado con éxito. ¡Ya puedes iniciar sesión!`, true);
            clearForm(registerForm);
            showLoginBtn.click();
        } else {
            const errorText = await response.text();
            showMessage(registerMessage, `Error al registrar: ${errorText || 'El email ya está en uso.'}`, false);
        }
    } catch (error) {
        console.error('Error al registrar:', error);
        showMessage(registerMessage, 'Error de conexión al servidor.', false);
    }
}

// Función que devuelve el header de autorización guardado
function getAuthHeaders() {
    if (authorizationHeader) {
        return {
            'Authorization': authorizationHeader
        };
    }
    return {};
}

// Cargar Plantas del Usuario
async function loadUserPlants(userId) {
    if (!userId) return;
    plantListDiv.innerHTML = '<p class="text-center text-gray-500 col-span-full">Cargando plantas...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/plantas/usuario/${userId}`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const plants = await response.json();
            renderPlants(plants);
        } else {
            const errorText = await response.text();
            const errorDetails = `Error al cargar plantas. Detalles: ${errorText || response.statusText}`;
            plantListDiv.innerHTML = `<p class="text-red-500">${errorDetails}</p>`;
            console.error('Error al cargar plantas:', response.status, errorDetails);
        }
    } catch (error) {
        plantListDiv.innerHTML = '<p class="text-red-500">Error de conexión al servidor.</p>';
        console.error('Error de red al cargar plantas:', error);
    }
}

// Renderizar Plantas en la UI
function renderPlants(plants) {
    plantListDiv.innerHTML = '';
    if (plants.length === 0) {
        plantListDiv.innerHTML = '<p class="text-center text-gray-500 col-span-full">No tienes plantas registradas. ¡Añade una!</p>';
        return;
    }

    plants.forEach(planta => {
        const plantCard = document.createElement('div');
        plantCard.className = 'plant-card bg-white rounded-lg shadow-md p-6 flex flex-col';
        const imageUrl = planta.fotoPlanta ? `data:image/jpeg;base64,${planta.fotoPlanta}` : `https://placehold.co/400x150/e5e7eb/6b7280?text=Sin+Foto`;
        
        plantCard.innerHTML = `
            <img src="${imageUrl}" alt="${planta.nombreComun}" class="w-full h-40 object-cover rounded mb-4">
            <h4 class="text-xl font-semibold text-green-800 mb-2">${planta.nombreComun}</h4>
            <p class="text-gray-600 text-sm mb-1">Científico: ${planta.nombreCientifico || 'N/A'}</p>
            <p class="text-gray-600 text-sm mb-1">Ubicación: ${planta.ubicacion || 'N/A'}</p>
            <p class="text-gray-600 text-sm mb-1">Último Riego: ${planta.ultimaFechaRiego ? new Date(planta.ultimaFechaRiego).toLocaleDateString() : 'Nunca'}</p>
            <p class="text-gray-600 text-sm mb-4">Frecuencia: ${planta.frecuenciaRiegoDias ? `${planta.frecuenciaRiegoDias} días` : 'No definida'}</p>
            <div class="flex flex-wrap gap-2 mt-auto">
                <button data-id="${planta.idPlanta}" class="view-details-btn bg-gray-500 hover:bg-gray-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-info-circle mr-1"></i>Detalles</button>
                <button data-id="${planta.idPlanta}" data-name="${planta.nombreComun}" class="water-plant-btn bg-blue-500 hover:bg-blue-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-tint mr-1"></i>Regar</button>
                <button data-id="${planta.idPlanta}" class="edit-plant-btn bg-yellow-500 hover:bg-yellow-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-edit mr-1"></i>Editar</button>
                <button data-id="${planta.idPlanta}" class="delete-plant-btn bg-red-500 hover:bg-red-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-trash-alt mr-1"></i>Eliminar</button>
            </div>
        `;
        plantListDiv.appendChild(plantCard);
    });

    document.querySelectorAll('.view-details-btn').forEach(button => button.addEventListener('click', (e) => showPlantDetails(e.target.dataset.id)));
    document.querySelectorAll('.water-plant-btn').forEach(button => button.addEventListener('click', (e) => showWaterPlantModal(e.target.dataset.id, e.target.dataset.name)));
    document.querySelectorAll('.edit-plant-btn').forEach(button => button.addEventListener('click', (e) => showPlantFormModal(e.target.dataset.id)));
    document.querySelectorAll('.delete-plant-btn').forEach(button => button.addEventListener('click', (e) => deletePlant(e.target.dataset.id)));
}

// Mostrar detalles de la planta en una alerta
async function showPlantDetails(plantId) {
    try {
        const response = await fetch(`${API_BASE_URL}/plantas/${plantId}`, { headers: getAuthHeaders() });
        if (response.ok) {
            const planta = await response.json();
            let details = `Detalles de la Planta:\n\n` +
                          `Nombre Común: ${planta.nombreComun || 'N/A'}\n` +
                          `Nombre Científico: ${planta.nombreCientifico || 'N/A'}\n` +
                          `Ubicación: ${planta.ubicacion || 'N/A'}\n` +
                          `Fecha de Adquisición: ${planta.fechaAdquisicion ? new Date(planta.fechaAdquisicion).toLocaleDateString() : 'N/A'}\n` +
                          `Notas: ${planta.notas || 'N/A'}\n` +
                          `Frecuencia de Riego: ${planta.frecuenciaRiegoDias ? `${planta.frecuenciaRiegoDias} días` : 'No definida'}\n` +
                          `Último Riego: ${planta.ultimaFechaRiego ? new Date(planta.ultimaFechaRiego).toLocaleString() : 'Nunca'}\n`;
            alert(details);
        } else {
            alert('No se pudieron cargar los detalles de la planta.');
        }
    } catch (error) {
        alert('Error de conexión al servidor.');
    }
}

// Mostrar/Ocultar Modal de Formulario de Planta
async function showPlantFormModal(plantId = null) {
    clearForm(plantForm);
    plantIdInput.value = '';
    plantUserIdInput.value = currentUserId;

    if (plantId) {
        plantFormTitle.textContent = 'Editar Planta';
        try {
            const response = await fetch(`${API_BASE_URL}/plantas/${plantId}`, { headers: getAuthHeaders() });
            if (response.ok) {
                const planta = await response.json();
                plantIdInput.value = planta.idPlanta;
                document.getElementById('nombreComun').value = planta.nombreComun;
                document.getElementById('nombreCientifico').value = planta.nombreCientifico || '';
                document.getElementById('ubicacion').value = planta.ubicacion || '';
                document.getElementById('fechaAdquisicion').value = planta.fechaAdquisicion || '';
                document.getElementById('notas').value = planta.notas || '';
                document.getElementById('frecuenciaRiegoDias').value = planta.frecuenciaRiegoDias || '';
            } else {
                alert('No se pudo cargar la planta para edición.');
                return;
            }
        } catch (error) {
            alert('Error de conexión al servidor.');
            return;
        }
    } else {
        plantFormTitle.textContent = 'Añadir Nueva Planta';
    }
    plantFormModal.classList.remove('hidden');
}

function hidePlantFormModal() {
    plantFormModal.classList.add('hidden');
}

// Enviar Formulario de Planta (Crear/Editar)
async function submitPlantForm(event) {
    event.preventDefault();
    const idPlanta = plantIdInput.value;
    const isEditing = !!idPlanta;

    const formData = new FormData();
    const plantData = {
        nombreComun: document.getElementById('nombreComun').value,
        nombreCientifico: document.getElementById('nombreCientifico').value,
        ubicacion: document.getElementById('ubicacion').value,
        fechaAdquisicion: document.getElementById('fechaAdquisicion').value,
        notas: document.getElementById('notas').value,
        frecuenciaRiegoDias: document.getElementById('frecuenciaRiegoDias').value ? parseInt(document.getElementById('frecuenciaRiegoDias').value) : null
    };

    formData.append('planta', JSON.stringify(plantData));
    formData.append('idUsuario', currentUserId);

    const fotoFile = fotoInput.files[0];
    if (fotoFile) {
        formData.append('foto', fotoFile);
    }

    try {
        const url = isEditing ? `${API_BASE_URL}/plantas/${idPlanta}` : `${API_BASE_URL}/plantas`;
        const method = isEditing ? 'PUT' : 'POST';

        // Para peticiones multipart/form-data, NO se debe establecer el 'Content-Type' manualmente.
        // El navegador lo hace automáticamente y añade el 'boundary' necesario.
        const headers = getAuthHeaders();

        const response = await fetch(url, {
            method: method,
            headers: headers, // Solo Authorization, sin Content-Type
            body: formData
        });

        if (response.ok) {
            alert(`Planta ${isEditing ? 'actualizada' : 'creada'} con éxito.`);
            hidePlantFormModal();
            loadUserPlants(currentUserId);
        } else {
            const errorText = await response.text();
            const errorDetails = `Error al guardar planta. Detalles: ${errorText || response.statusText}`;
            alert(errorDetails);
            console.error('Error al guardar planta:', response.status, errorDetails);
        }
    } catch (error) {
        console.error('Error de red al enviar formulario de planta:', error);
        alert('Error de conexión al servidor.');
    }
}

// Eliminar Planta
async function deletePlant(plantId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta planta?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/plantas/${plantId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            alert('Planta eliminada con éxito.');
            loadUserPlants(currentUserId);
        } else {
            const errorText = await response.text();
            alert(`Error al eliminar planta: ${errorText || response.statusText}`);
        }
    } catch (error) {
        alert('Error de conexión al servidor.');
    }
}

// Mostrar/Ocultar Modal de Riego
async function showWaterPlantModal(plantId, plantName) {
    clearForm(waterPlantForm);
    waterPlantIdInput.value = plantId;
    waterPlantNameSpan.textContent = plantName;
    waterPlantModal.classList.remove('hidden');
}

function hideWaterPlantModal() {
    waterPlantModal.classList.add('hidden');
}

// Registrar Riego
async function submitWaterPlantForm(event) {
    event.preventDefault();

    // El nuevo formato de datos es plano y más simple
    const riegoData = {
        plantaId: parseInt(waterPlantIdInput.value), // Enviamos solo el ID
        cantidadAguaMl: parseFloat(document.getElementById('cantidadAguaMl').value),
        observaciones: document.getElementById('observacionesRiego').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/riegos`, {
            method: 'POST',
            headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' },
            body: JSON.stringify(riegoData)
        });

        if (response.ok) {
            alert('Riego registrado con éxito.');
            hideWaterPlantModal();
            loadUserPlants(currentUserId);
            loadUserNotifications(currentUserId);
        } else {
            const errorText = await response.text();
            alert(`Error al registrar riego: ${errorText || 'La planta especificada no fue encontrada.'}`);
        }
    } catch (error) {
        alert('Error de conexión al servidor.');
    }
}

// Cargar Notificaciones del Usuario
async function loadUserNotifications(userId) {
    if (!userId) return;
    notificationListDiv.innerHTML = '<p class="text-center text-gray-500">Cargando notificaciones...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/notificaciones/usuario/${userId}`, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const notifications = await response.json();
            renderNotifications(notifications);
        } else {
            const errorText = await response.text();
            const errorDetails = `Error al cargar notificaciones. Detalles: ${errorText || response.statusText}`;
            notificationListDiv.innerHTML = `<p class="text-red-500">${errorDetails}</p>`;
            console.error('Error al cargar notificaciones:', response.status, errorDetails);
        }
    } catch (error) {
        notificationListDiv.innerHTML = '<p class="text-red-500">Error de conexión al servidor.</p>';
        console.error('Error de red al cargar notificaciones:', error);
    }
}

// Renderizar Notificaciones en la UI
function renderNotifications(notifications) {
    notificationListDiv.innerHTML = '';
    if (notifications.length === 0) {
        notificationListDiv.innerHTML = '<p class="text-center text-gray-500">No tienes notificaciones.</p>';
        return;
    }

    notifications.sort((a, b) => new Date(b.fechaNotificacion) - new Date(a.fechaNotificacion));

    notifications.forEach(notif => {
        const notifItem = document.createElement('div');
        notifItem.className = `notification-item ${notif.leida ? 'read' : ''}`;
        notifItem.innerHTML = `
            <span>${new Date(notif.fechaNotificacion).toLocaleDateString()} - ${notif.textoNotificacion}</span>
            ${!notif.leida ? `<button data-id="${notif.idNotificacion}" class="mark-read-btn bg-blue-500 hover:bg-blue-600 text-white text-xs py-1 px-2 rounded">Leída</button>` : ''}
        `;
        notificationListDiv.appendChild(notifItem);
    });

    document.querySelectorAll('.mark-read-btn').forEach(button => button.addEventListener('click', (e) => markNotificationAsRead(e.target.dataset.id)));
}

// Marcar Notificación como Leída
async function markNotificationAsRead(notificationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/notificaciones/${notificationId}/marcar-leida`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            loadUserNotifications(currentUserId);
        } else {
            alert('Error al marcar la notificación como leída.');
        }
    } catch (error) {
        alert('Error de conexión al servidor.');
    }
}

// --- Event Listeners ---

showLoginBtn.addEventListener('click', () => {
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
    clearForm(loginForm);
    clearForm(registerForm);
});

showRegisterBtn.addEventListener('click', () => {
    registerForm.classList.remove('hidden');
    loginForm.classList.add('hidden');
    clearForm(loginForm);
    clearForm(registerForm);
});

loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    authenticateUser(email, password);
});

registerForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    registerUser(name, email, password);
});

logoutBtn.addEventListener('click', () => {
    showAuthSection();
    showMessage(loginMessage, 'Sesión cerrada correctamente.', true);
});

addPlantBtn.addEventListener('click', () => showPlantFormModal());
cancelPlantFormBtn.addEventListener('click', hidePlantFormModal);
plantForm.addEventListener('submit', submitPlantForm);
cancelWaterPlantBtn.addEventListener('click', hideWaterPlantModal);
waterPlantForm.addEventListener('submit', submitWaterPlantForm);

// --- Inicialización ---
document.addEventListener('DOMContentLoaded', showAuthSection);