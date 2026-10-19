// Utilidades compartidas por todas las páginas internas
const App = (() => {
  const ses = JSON.parse(sessionStorage.getItem('ses') || 'null');
  const aqui = location.pathname.split('/').pop() || 'index.html';

  // Evita redirección si ya está en la página de login
  const esLogin = aqui === '' || aqui === 'index.html';
  if (!ses && !esLogin) {
    location.replace('index.html');
  }

  // Normalización de rol
  const rolRaw = (ses?.rol || '').toUpperCase();
  const admin = rolRaw === 'ADMINISTRADOR' || rolRaw === 'ADMIN';
  const rolNorm = admin ? 'ADMINISTRADOR' : (rolRaw.includes('MEDICO') ? 'MEDICO' : 'RECEPCIONISTA');

  const $ = s => document.querySelector(s);
  const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  const badge = e => `<span class="badge ${e === true || e === 'ACTIVO' ? 'on-b' : 'off-b'}">${e === true || e === 'ACTIVO' ? 'Activo' : 'Inactivo'}</span>`;

  function toast(m, err) {
    const t = $('#toast');
    if (!t) return;
    t.textContent = m;
    t.style.background = err ? '#B23A2E' : '#163247';
    t.style.display = 'block';
    clearTimeout(toast.h);
    toast.h = setTimeout(() => t.style.display = 'none', 3800);
  }

  function logout() {
    sessionStorage.removeItem('ses');
    location.replace('index.html');
  }

  async function api(path, opt = {}) {
    const headers = {
      'Content-Type': 'application/json',
      ...(ses?.auth ? { 'Authorization': ses.auth } : {}),
      ...(opt.headers || {})
    };

    const r = await fetch('/api' + path, { ...opt, headers });
    let b = null;
    try { b = await r.json(); } catch (e) {}

    if (r.status === 401) {
      logout();
      throw new Error('Sesión expirada');
    }
    if (!r.ok) {
      const errorMsg = (b && (b.error || (b.errores && Object.values(b.errores).join(', ')))) || ('Error ' + r.status);
      throw new Error(errorMsg);
    }
    return b;
  }

  // Archivos del módulo Médicos y especialidades
  const ARCHIVOS_MOD = ['medicos.html', 'medicos-especialidades.html', 'horarios.html', 'consultorios.html', 'vacaciones.html'];
  const pagMedicos = aqui === 'medicos.html' ? 'medicos.html' : 'medicos-especialidades.html';

  // Menú principal por rol
  const LINKS = {
    ADMINISTRADOR: [['index.html', 'Panel'], [pagMedicos, 'Médicos y especialidades'], ['pacientes.html', 'Pacientes'], ['citas.html', 'Citas']],
    MEDICO: [[pagMedicos, 'Médicos y especialidades'], ['pacientes.html', 'Pacientes']],
    RECEPCIONISTA: [[pagMedicos, 'Médicos y especialidades'], ['pacientes.html', 'Pacientes'], ['citas.html', 'Citas']]
  };

  // Submenú del módulo Médicos y especialidades
  const MOD = [
    [`${pagMedicos}#medicos`, 'Médicos'],
    [`${pagMedicos}#especialidades`, 'Especialidades'],
    ['horarios.html', 'Horarios y disponibilidad'],
    ['consultorios.html', 'Consultorios'],
    ['vacaciones.html', 'Vacaciones y permisos']
  ];

  function layout(titulo, modulo) {
    if (!ses && !esLogin) return;

    const topEl = $('#top');
    if (!topEl) return;

    const pagActual = aqui || 'index.html';

    // 1. Render de Header y Menú Principal
    topEl.innerHTML =
      `<header><h1>${esc(titulo)}</h1><div class="who"><span>${esc(ses?.username || '')} · ${esc(ses?.rol || '')}</span><button onclick="App.logout()">Cerrar sesión</button></div></header>` +
      `<nav>${(LINKS[rolNorm] || LINKS.RECEPCIONISTA).map(([h, t]) => {
        const esModuloMedicos = ARCHIVOS_MOD.includes(pagActual) && (h === pagMedicos || h === 'medicos-especialidades.html' || h === 'medicos.html');
        const esPaginaActual = h === pagActual;
        const activa = esPaginaActual || esModuloMedicos;
        return `<a href="${h}" class="${activa ? 'on' : ''}">${t}</a>`;
      }).join('')}</nav>` +
      (modulo ? '<nav class="sub" id="sub"></nav>' : '');

    // 2. Render de Submenú de Médicos
    if (modulo) {
      const pintarSub = () => {
        const subEl = $('#sub');
        if (!subEl) return;

        const hashActual = location.hash || ((pagActual === 'medicos.html' || pagActual === 'medicos-especialidades.html') ? '#medicos' : '');

        subEl.innerHTML = MOD
          .filter(([h]) => admin || !h.includes('vacaciones.html')) // Solo ADMINISTRADOR ve Vacaciones y permisos
          .map(([h, t]) => {
            const [hPag, hHash] = h.split('#');
            const esMismaPagina = pagActual === hPag || (ARCHIVOS_MOD.slice(0, 2).includes(pagActual) && ARCHIVOS_MOD.slice(0, 2).includes(hPag));
            const esMismoHash = !hHash || (hashActual === '#' + hHash);
            const activa = esMismaPagina && esMismoHash;

            return `<a href="${h}" class="${activa ? 'on' : ''}">${t}</a>`;
          }).join('');
      };

      pintarSub();
      window.removeEventListener('hashchange', window._appHashHandler);
      window._appHashHandler = pintarSub;
      window.addEventListener('hashchange', window._appHashHandler);
    }
  }

  return { ses, admin, $, esc, badge, toast, api, logout, layout };
})();