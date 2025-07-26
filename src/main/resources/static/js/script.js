// src/main/resources/static/js/script.js

// --- Variables Globales y Selectores DOM ---
const API_BASE_URL = 'http://localhost:8080/api'; // URL base de tu backend Spring Boot

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
const plantUserIdInput = document.getElementById('plant-user-id'); // Campo oculto para el ID del usuario
const fotoInput = document.getElementById('foto'); // Input de tipo file para la foto

// Modal y formulario de riego
const waterPlantModal = document.getElementById('water-plant-modal');
const waterPlantForm = document.getElementById('water-plant-form');
const waterPlantNameSpan = document.getElementById('water-plant-name');
const waterPlantIdInput = document.getElementById('water-plant-id');
const cancelWaterPlantBtn = document.getElementById('cancel-water-plant-btn');

// Estado de la aplicación
let currentUserId = null; // Almacenará el ID del usuario logueado
let currentUserName = null; // Almacenará el nombre del usuario logueado

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
    loginForm.classList.remove('hidden'); // Mostrar login por defecto
    registerForm.classList.add('hidden');
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
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        const message = await response.text(); // La respuesta es un String

        if (response.ok) {
            // En un sistema real, el login devolvería un token JWT y el ID del usuario.
            // Para simplificar, haremos una llamada adicional para obtener el ID del usuario.
            // NOTA: Esta llamada adicional no es ideal para producción por seguridad y eficiencia.
            const userResponse = await fetch(`${API_BASE_URL}/usuarios?email=${email}`); // Asumiendo que existe un endpoint GET /usuarios?email=
            const users = await userResponse.json();
            const loggedInUser = users.find(u => u.email === email); // Buscar el usuario por email
            
            if (loggedInUser) {
                currentUserId = loggedInUser.idUsuario;
                currentUserName = loggedInUser.nombre; // Guardar el nombre también
                showMessage(loginMessage, `¡Bienvenido, ${currentUserName}!`, true);
                setTimeout(showAppSection, 1000); // Retraso para ver el mensaje
            } else {
                showMessage(loginMessage, 'Error al obtener datos del usuario después del login.', false);
            }
        } else {
            showMessage(loginMessage, message, false);
        }
    } catch (error) {
        console.error('Error al iniciar sesión:', error);
        showMessage(loginMessage, 'Error de conexión al servidor.', false);
    }
}

// Registrar Usuario
async function registerUser(name, email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/registrar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ nombre: name, email, password })
        });

        if (response.ok) {
            const newUser = await response.json();
            showMessage(registerMessage, `Usuario ${newUser.nombre} registrado con éxito. ¡Ya puedes iniciar sesión!`, true);
            clearForm(registerForm);
            showLoginBtn.click(); // Vuelve a mostrar el formulario de login
        } else {
            const errorText = await response.text();
            showMessage(registerMessage, `Error al registrar: ${errorText || response.statusText}`, false);
        }
    } catch (error) {
        console.error('Error al registrar:', error);
        showMessage(registerMessage, 'Error de conexión al servidor.', false);
    }
}

// Función para obtener credenciales de autenticación básica
function getAuthHeaders() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    console.log("DEBUG: Email desde input:", email);
    console.log("DEBUG: Contraseña desde input:", password);

    if (email && password) {
        const credentialsString = `${email}:${password}`;
        console.log("DEBUG: Cadena de credenciales (email:password):", credentialsString);

        try {
            // Alternativa robusta para Base64 con caracteres UTF-8
            // encodeURIComponent codifica los caracteres especiales a %XX
            // unescape los convierte de nuevo a una "cadena binaria" Latin-1 compatible con btoa
            const base64 = btoa(unescape(encodeURIComponent(credentialsString)));
            console.log("DEBUG: Cadena codificada Base64 (alternativa):", base64);
            
            return {
                'Authorization': `Basic ${base64}`,
                'Content-Type': 'application/json' // Por defecto para JSON, se sobrescribe para form-data si es necesario
            };
        } catch (e) {
            console.error("ERROR: Fallo al codificar a Base64. Esto suele ocurrir con caracteres no ASCII en la contraseña o email.", e);
            showMessage(loginMessage, 'Error interno: Problema con la codificación de credenciales. Intenta con una contraseña sin caracteres especiales.', false);
            return {};
        }
    }
    console.log("DEBUG: Email o contraseña vacíos, no se enviarán cabeceras de autenticación.");
    return {}; // Retorna un objeto vacío si no hay credenciales
}


// Cargar Plantas del Usuario
async function loadUserPlants(userId) {
    if (!userId) {
        plantListDiv.innerHTML = '<p class="text-center text-gray-500 col-span-full">Por favor, inicia sesión para ver tus plantas.</p>';
        return;
    }
    plantListDiv.innerHTML = '<p class="text-center text-gray-500 col-span-full">Cargando plantas...</p>';
    try {
        const response = await fetch(`${API_BASE_URL}/plantas/usuario/${userId}`, {
            headers: getAuthHeaders() // Envía credenciales para ruta protegida
        });
        if (response.ok) {
            const plants = await response.json();
            renderPlants(plants);
        } else if (response.status === 401 || response.status === 403) {
            plantListDiv.innerHTML = '<p class="text-center text-red-500 col-span-full">Acceso denegado. Por favor, inicia sesión de nuevo.</p>';
            console.error('Acceso denegado al cargar plantas:', response.status);
            // Opcional: forzar logout si las credenciales expiraron o son inválidas
            // logoutBtn.click();
        } else {
            plantListDiv.innerHTML = '<p class="text-center text-red-500 col-span-full">Error al cargar las plantas.</p>';
            console.error('Error al cargar plantas:', response.status, response.statusText);
        }
    } catch (error) {
        plantListDiv.innerHTML = '<p class="text-center text-red-500 col-span-full">Error de conexión al servidor al cargar plantas.</p>';
        console.error('Error de red al cargar plantas:', error);
    }
}

// Renderizar Plantas en la UI
function renderPlants(plants) {
    plantListDiv.innerHTML = ''; // Limpiar lista existente
    if (plants.length === 0) {
        plantListDiv.innerHTML = '<p class="text-center text-gray-500 col-span-full">No tienes plantas registradas. ¡Añade una!</p>';
        return;
    }

    plants.forEach(planta => {
        const plantCard = document.createElement('div');
        plantCard.className = 'plant-card bg-white rounded-lg shadow-md p-6 flex flex-col';
        // Convertir byte[] (base64) a URL de imagen
        const imageUrl = planta.fotoPlanta ? `data:image/jpeg;base64,${planta.fotoPlanta}` : `https://placehold.co/400x150/e5e7eb/6b7280?text=Sin+Foto`;
        
        plantCard.innerHTML = `
            <img src="${imageUrl}" alt="${planta.nombreComun}" class="w-full h-40 object-cover rounded mb-4">
            <h4 class="text-xl font-semibold text-green-800 mb-2">${planta.nombreComun}
                ${planta.necesitaChequeoRiego ? '<span class="needs-watering-indicator" title="Necesita chequeo de riego"></span>' : ''}
            </h4>
            <p class="text-gray-600 text-sm mb-1">Científico: ${planta.nombreCientifico || 'N/A'}</p>
            <p class="text-gray-600 text-sm mb-1">Ubicación: ${planta.ubicacion || 'N/A'}</p>
            <p class="text-gray-600 text-sm mb-1">Último Riego: ${planta.ultimaFechaRiego ? new Date(planta.ultimaFechaRiego).toLocaleDateString() : 'Nunca'}</p>
            <p class="text-gray-600 text-sm mb-4">Frecuencia: ${planta.frecuenciaRiegoDias ? `${planta.frecuenciaRiegoDias} días` : 'No definida'}</p>
            <div class="flex flex-wrap gap-2 mt-auto">
                <button data-id="${planta.idPlanta}" class="view-details-btn bg-blue-500 hover:bg-blue-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-info-circle mr-1"></i>Detalles</button>
                <button data-id="${planta.idPlanta}" data-name="${planta.nombreComun}" class="water-plant-btn bg-blue-500 hover:bg-blue-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-tint mr-1"></i>Regar</button>
                <button data-id="${planta.idPlanta}" class="edit-plant-btn bg-yellow-500 hover:bg-yellow-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-edit mr-1"></i>Editar</button>
                <button data-id="${planta.idPlanta}" class="delete-plant-btn bg-red-500 hover:bg-red-600 text-white text-sm py-1 px-3 rounded-md transition duration-300"><i class="fas fa-trash-alt mr-1"></i>Eliminar</button>
            </div>
        `;
        plantListDiv.appendChild(plantCard);
    });

    // Añadir event listeners a los botones de cada planta
    document.querySelectorAll('.water-plant-btn').forEach(button => {
        button.addEventListener('click', (e) => showWaterPlantModal(e.target.dataset.id, e.target.dataset.name));
    });
    document.querySelectorAll('.edit-plant-btn').forEach(button => {
        button.addEventListener('click', (e) => showPlantFormModal(e.target.dataset.id));
    });
    document.querySelectorAll('.delete-plant-btn').forEach(button => {
        button.addEventListener('click', (e) => deletePlant(e.target.dataset.id));
    });
    // Puedes implementar la lógica para 'view-details-btn' para mostrar más información en un modal o nueva página
}

// Mostrar/Ocultar Modal de Formulario de Planta
async function showPlantFormModal(plantId = null) {
    clearForm(plantForm);
    plantIdInput.value = ''; // Limpiar ID previo
    plantUserIdInput.value = currentUserId; // Asegurar que el ID de usuario esté siempre presente

    if (plantId) {
        plantFormTitle.textContent = 'Editar Planta';
        try {
            const response = await fetch(`${API_BASE_URL}/plantas/${plantId}`, {
                headers: getAuthHeaders() // Envía credenciales para ruta protegida
            });
            if (response.ok) {
                const planta = await response.json();
                plantIdInput.value = planta.idPlanta;
                document.getElementById('nombreComun').value = planta.nombreComun;
                document.getElementById('nombreCientifico').value = planta.nombreCientifico || '';
                document.getElementById('ubicacion').value = planta.ubicacion || '';
                document.getElementById('fechaAdquisicion').value = planta.fechaAdquisicion || '';
                document.getElementById('notas').value = planta.notas || '';
                document.getElementById('frecuenciaRiegoDias').value = planta.frecuenciaRiegoDias || '';
                // La foto no se precarga en el input file por seguridad, el usuario deberá subir una nueva si quiere cambiarla
            } else if (response.status === 401 || response.status === 403) {
                alert('Acceso denegado. Por favor, inicia sesión de nuevo.');
                console.error('Acceso denegado al cargar planta para edición:', response.status);
            } else {
                console.error('Error al cargar datos de la planta para edición:', response.status, response.statusText);
                alert('No se pudo cargar la planta para edición.');
                return;
            }
        } catch (error) {
            console.error('Error de red al cargar planta para edición:', error);
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
    const isEditing = !!idPlanta; // Si hay ID, estamos editando

    const formData = new FormData();
    const plantData = {
        usuario: { idUsuario: currentUserId }, // Aseguramos que el usuario esté asociado
        nombreComun: document.getElementById('nombreComun').value,
        nombreCientifico: document.getElementById('nombreCientifico').value,
        ubicacion: document.getElementById('ubicacion').value,
        fechaAdquisicion: document.getElementById('fechaAdquisicion').value,
        notas: document.getElementById('notas').value,
        frecuenciaRiegoDias: document.getElementById('frecuenciaRiegoDias').value ? parseInt(document.getElementById('frecuenciaRiegoDias').value) : null
    };

    formData.append('planta', JSON.stringify(plantData));

    const fotoFile = fotoInput.files[0];
    if (fotoFile) {
        formData.append('foto', fotoFile);
    } else if (isEditing) {
        // Si estamos editando y no se sube nueva foto, no enviar el campo 'foto'
        // para que el backend no intente actualizarlo a null si no es necesario.
        // Esto depende de la lógica del backend para PUT.
        // Si el backend espera 'foto' incluso si está vacío, se podría añadir formData.append('foto', '');
    }

    try {
        const url = isEditing ? `${API_BASE_URL}/plantas/${idPlanta}` : `${API_BASE_URL}/plantas`;
        const method = isEditing ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: getAuthHeaders(), // Envía credenciales para ruta protegida
            // No se necesita 'Content-Type': 'multipart/form-data' aquí; fetch lo configura automáticamente con FormData
            body: formData
        });

        if (response.ok) {
            alert(`Planta ${isEditing ? 'actualizada' : 'creada'} con éxito.`);
            hidePlantFormModal();
            loadUserPlants(currentUserId); // Recargar la lista de plantas
        } else if (response.status === 401 || response.status === 403) {
            alert('Acceso denegado. Por favor, inicia sesión de nuevo.');
            console.error('Acceso denegado al guardar planta:', response.status);
        }
        else {
            const errorText = await response.text();
            alert(`Error al ${isEditing ? 'actualizar' : 'crear'} planta: ${errorText || response.statusText}`);
            console.error(`Error al ${isEditing ? 'actualizar' : 'crear'} planta:`, response.status, errorText);
        }
    } catch (error) {
        console.error('Error de red al enviar formulario de planta:', error);
        alert('Error de conexión al servidor.');
    }
}

// Eliminar Planta
async function deletePlant(plantId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta planta y todos sus riegos asociados?')) {
        return;
    }
    try {
        const response = await fetch(`${API_BASE_URL}/plantas/${plantId}`, {
            method: 'DELETE',
            headers: getAuthHeaders() // Envía credenciales para ruta protegida
        });

        if (response.ok) {
            alert('Planta eliminada con éxito.');
            loadUserPlants(currentUserId); // Recargar la lista de plantas
        } else if (response.status === 401 || response.status === 403) {
            alert('Acceso denegado. Por favor, inicia sesión de nuevo.');
            console.error('Acceso denegado al eliminar planta:', response.status);
        } else {
            const errorText = await response.text();
            alert(`Error al eliminar planta: ${errorText || response.statusText}`);
            console.error('Error al eliminar planta:', response.status, errorText);
        }
    }
    catch (error) {
        console.error('Error de red al eliminar planta:', error);
        alert('Error de conexión al servidor.');
    }
}

// Mostrar/Ocultar Modal de Riego
async function showWaterPlantModal(plantId, plantName) {
    clearForm(waterPlantForm);
    waterPlantIdInput.value = plantId;
    waterPlantNameSpan.textContent = plantName; // Mostrar el nombre de la planta en el modal
    waterPlantModal.classList.remove('hidden');
    // Ya no es necesario hacer un fetch aquí si el nombre ya se pasa
}

function hideWaterPlantModal() {
    waterPlantModal.classList.add('hidden');
}

// Registrar Riego
async function submitWaterPlantForm(event) {
    event.preventDefault();

    const idPlanta = waterPlantIdInput.value;
    const cantidadAguaMl = document.getElementById('cantidadAguaMl').value;
    const observaciones = document.getElementById('observacionesRiego').value;

    const riegoData = {
        planta: { idPlanta: idPlanta },
        cantidadAguaMl: parseFloat(cantidadAguaMl),
        observaciones: observaciones
    };

    try {
        const response = await fetch(`${API_BASE_URL}/riegos`, {
            method: 'POST',
            headers: {
                ...getAuthHeaders(), // Incluye las cabeceras de autenticación
                'Content-Type': 'application/json' // Asegura el Content-Type para JSON
            },
            body: JSON.stringify(riegoData)
        });

        if (response.ok) {
            alert('Riego registrado con éxito.');
            hideWaterPlantModal();
            loadUserPlants(currentUserId); // Recargar la lista de plantas para actualizar última fecha de riego
            loadUserNotifications(currentUserId); // Recargar notificaciones para ver la confirmación
        } else if (response.status === 401 || response.status === 403) {
            alert('Acceso denegado. Por favor, inicia sesión de nuevo.');
            console.error('Acceso denegado al registrar riego:', response.status);
        } else {
            const errorText = await response.text();
            alert(`Error al registrar riego: ${errorText || response.statusText}`);
            console.error('Error al registrar riego:', response.status, errorText);
        }
    } catch (error) {
        console.error('Error de red al registrar riego:', error);
        alert('Error de conexión al servidor.');
    }
}

// Cargar Notificaciones del Usuario
async function loadUserNotifications(userId) {
    if (!userId) {
        notificationListDiv.innerHTML = '<p class="text-center text-gray-500">Inicia sesión para ver tus notificaciones.</p>';
        return;
    }
    notificationListDiv.innerHTML = '<p class="text-center text-gray-500">Cargando notificaciones...</p>';
    try {
        const response = await fetch(`${API_BASE_URL}/notificaciones/usuario/${userId}`, {
            headers: getAuthHeaders() // Envía credenciales para ruta protegida
        });
        if (response.ok) {
            const notifications = await response.json();
            renderNotifications(notifications);
        } else if (response.status === 401 || response.status === 403) {
            notificationListDiv.innerHTML = '<p class="text-center text-red-500">Acceso denegado. Por favor, inicia sesión de nuevo.</p>';
            console.error('Acceso denegado al cargar notificaciones:', response.status);
        } else {
            notificationListDiv.innerHTML = '<p class="text-center text-red-500">Error al cargar notificaciones.</p>';
            console.error('Error al cargar notificaciones:', response.status, response.statusText);
        }
    } catch (error) {
        console.error('Error de red al cargar notificaciones:', error);
        alert('Error de conexión al servidor.');
    }
}

// Renderizar Notificaciones en la UI
function renderNotifications(notifications) {
    notificationListDiv.innerHTML = '';
    if (notifications.length === 0) {
        notificationListDiv.innerHTML = '<p class="text-center text-gray-500">No tienes notificaciones.</p>';
        return;
    }

    // Ordenar notificaciones: no leídas primero, luego por fecha descendente
    notifications.sort((a, b) => {
        if (a.leida === b.leida) {
            return new Date(b.fechaNotificacion) - new Date(a.fechaNotificacion);
        }
        return a.leida ? 1 : -1; // Las no leídas (false) van antes que las leídas (true)
    });

    notifications.forEach(notif => {
        const notifItem = document.createElement('div');
        notifItem.className = `notification-item ${notif.leida ? 'read' : ''}`;
        notifItem.innerHTML = `
            <span>${new Date(notif.fechaNotificacion).toLocaleDateString()} - ${notif.textoNotificacion}</span>
            ${!notif.leida ? `<button data-id="${notif.idNotificacion}" class="mark-read-btn">Marcar como leída</button>` : ''}
        `;
        notificationListDiv.appendChild(notifItem);
    });

    document.querySelectorAll('.mark-read-btn').forEach(button => {
        button.addEventListener('click', (e) => markNotificationAsRead(e.target.dataset.id));
    });
}

// Marcar Notificación como Leída
async function markNotificationAsRead(notifId) {
    try {
        const response = await fetch(`${API_BASE_URL}/notificaciones/${notifId}/marcar-leida`, {
            method: 'PUT',
            headers: getAuthHeaders() // Envía credenciales para ruta protegida
        });
        if (response.ok) {
            loadUserNotifications(currentUserId); // Recargar notificaciones
            loadUserPlants(currentUserId); // Recargar plantas por si alguna notificación era de riego
        } else if (response.status === 401 || response.status === 403) {
            alert('Acceso denegado. Por favor, inicia sesión de nuevo.');
            console.error('Acceso denegado al marcar notificación como leída:', response.status);
        } else {
            console.error('Error al marcar notificación como leída:', response.status, response.statusText);
            alert('No se pudo marcar la notificación como leída.');
        }
    } catch (error) {
        console.error('Error de red al marcar notificación como leída:', error);
        alert('Error de conexión al servidor.');
    }
}


// --- Event Listeners ---

// Autenticación
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
    currentUserId = null;
    currentUserName = null;
    plantListDiv.innerHTML = ''; // Limpiar plantas
    notificationListDiv.innerHTML = ''; // Limpiar notificaciones
    showAuthSection();
    showMessage(loginMessage, 'Sesión cerrada correctamente.', true);
});

// Gestión de Plantas
addPlantBtn.addEventListener('click', () => showPlantFormModal());
cancelPlantFormBtn.addEventListener('click', hidePlantFormModal);
plantForm.addEventListener('submit', submitPlantForm);
cancelWaterPlantBtn.addEventListener('click', hideWaterPlantModal);
waterPlantForm.addEventListener('submit', submitWaterPlantForm);


// --- Inicialización ---
// Al cargar la página, mostrar la sección de autenticación por defecto
document.addEventListener('DOMContentLoaded', showAuthSection);
