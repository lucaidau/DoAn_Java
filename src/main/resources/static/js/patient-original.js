'use strict';
(() => {
  const API = '/patient/api/profile';
  let profile = {};
  let identity = {};
  const $ = (selector, root = document) => root.querySelector(selector);
  const all = (selector, root = document) => [...root.querySelectorAll(selector)];
  const display = (value) => value == null || value === '' ? 'Chưa cập nhật' : String(value);
  const show = (title, message) => {
    const box = document.createElement('div');
    box.setAttribute('role', 'alert');
    box.className = 'fixed bottom-6 right-6 z-[100] max-w-sm rounded-xl bg-white text-gray-800 shadow-xl border border-gray-200 px-5 py-4 text-sm';
    box.textContent = title + ': ' + message;
    document.body.append(box);
    setTimeout(() => box.remove(), 5500);
  };
  const date = (iso) => { if (!iso) return 'Chưa cập nhật'; const p = iso.split('-'); return p.length === 3 ? `${p[2]}/${p[1]}/${p[0]}` : iso; };
  const replaceCard = (section, label, value) => {
    const s = all('section').find(el => (el.querySelector('h3')?.textContent || '').includes(section));
    const card = s && all('div.bg-surface-container-low', s).find(el => el.querySelector('p')?.textContent.trim() === label);
    if (card) {
      const txt = all('p,span', card).find(el => el !== card.querySelector('p') && (el.className.includes('font-semibold') || el.className.includes('font-bold')));
      if (txt) txt.textContent = display(value);
    }
  };
  const setText = (el, text) => { if (el) el.textContent = display(text); };
  function render() {
    const left = $('aside');
    setText(left?.querySelector('h2'), identity.fullName);
    setText(left?.querySelector('p'), `Mã BN: #BN-${identity.id}`);
    const quick = left?.querySelector('.bg-surface-container-low');
    if (quick) {
      const spans = all('span', quick).filter(e => !e.className.includes('material-symbols-outlined'));
      if (spans[0]) setText(spans[0], identity.phone);
      if (spans[1]) setText(spans[1], identity.email);
    }
    replaceCard('Thông Tin Cá Nhân Cơ Bản', 'Họ và tên khai sinh', identity.fullName?.toUpperCase());
    replaceCard('Thông Tin Cá Nhân Cơ Bản', 'Ngày sinh', date(identity.birthDate));
    replaceCard('Thông Tin Cá Nhân Cơ Bản', 'Giới tính sinh học', identity.gender);
    replaceCard('Thông Tin Cá Nhân Cơ Bản', 'Số CCCD / Mã định danh', profile.citizenId ? '******' + profile.citizenId.slice(-4) : 'Chưa cập nhật');
    replaceCard('Thông Tin Cá Nhân Cơ Bản', 'Mã bệnh nhân', '#BN-' + identity.id);
    const basic = all('section').find(e => e.querySelector('h3')?.textContent.includes('Thông Tin Cá Nhân Cơ Bản'));
    const blood = basic && all('div.bg-surface-container-low', basic).find(e => e.querySelector('p')?.textContent.trim() === 'Nhóm máu');
    if (blood) setText(blood.querySelector('span.font-bold'), profile.bloodType);
    replaceCard('Thông Tin Liên Hệ', 'Số điện thoại chính', identity.phone);
    replaceCard('Thông Tin Liên Hệ', 'Địa chỉ Email', identity.email);
    replaceCard('Thông Tin Liên Hệ', 'Địa chỉ thường trú', profile.address);
    const contact = all('section').find(e => e.querySelector('h3')?.textContent.includes('Thông Tin Liên Hệ'));
    const emergencyName = contact?.querySelector(
    '.bg-secondary-container\\/20 p.font-semibold'
);
    if (emergencyName) {
      emergencyName.textContent = display(profile.emergencyContactName) + (profile.emergencyContactRelation ? ' (Quan hệ: ' + profile.emergencyContactRelation + ')' : '');
    }
    const emergencyPhone = contact?.querySelector('a[href^="tel:"]');
    if (emergencyPhone) {
      emergencyPhone.href = profile.emergencyContactPhone ? 'tel:' + profile.emergencyContactPhone : '#';
      setText(emergencyPhone.querySelector('span:last-child'), profile.emergencyContactPhone);
    }
    replaceCard('Thẻ BHYT Thiết Yếu', 'Mã thẻ BHYT', profile.healthInsuranceNumber);
    replaceCard('Thẻ BHYT Thiết Yếu', 'Nơi ĐKKCB ban đầu', profile.insuranceHospital);
    replaceCard('Thẻ BHYT Thiết Yếu', 'Thời hạn sử dụng', [date(profile.insuranceStart), date(profile.insuranceExpiry)].join(' - '));
    // Không đánh dấu trạng thái xác minh giả định từ dữ liệu mẫu.
    all('span').filter(e => e.children.length === 0 && e.textContent.includes('Khớp thẻ CCCD gắn chip')).forEach(e => e.textContent = 'Thông tin tài khoản tự khai báo');
    // Trạng thái xác minh/hiệu lực trong HTML mẫu không đồng nghĩa đã kiểm tra với cơ quan nhà nước.
    all('span').filter(e => /Đã định danh VNeID|Đã xác thực đồng bộ|Bộ Công An chứng thực|Đã xác minh OTP chính chủ|Đang hiệu lực/.test(e.textContent.trim()) && e.children.length === 0).forEach(e => {
      e.textContent = 'Chưa xác minh trên hệ thống';
    });
  }
  const fields = [
    ['address', 'Địa chỉ thường trú', 'text'], ['citizenId', 'CCCD (chỉ nhập khi cần)', 'text'],
    ['bloodType', 'Nhóm máu', 'select'], ['allergies', 'Thông tin dị ứng', 'textarea'],
    ['medicalHistory', 'Tiền sử bệnh', 'textarea'], ['emergencyContactName', 'Tên người liên hệ khẩn cấp', 'text'],
    ['emergencyContactRelation', 'Quan hệ với bệnh nhân', 'text'], ['emergencyContactPhone', 'Điện thoại khẩn cấp', 'tel'],
    ['healthInsuranceNumber', 'Mã thẻ BHYT', 'text'], ['insuranceHospital', 'Nơi đăng ký KCB ban đầu', 'text'],
    ['insuranceStart', 'Ngày BHYT bắt đầu', 'date'], ['insuranceExpiry', 'Ngày BHYT hết hạn', 'date']
  ];
  const modal = document.createElement('div');
  modal.id = 'care-profile-modal';
  modal.className = 'hidden fixed inset-0 z-[110] bg-black/40 overflow-y-auto p-4';
  modal.innerHTML = `<div class="max-w-2xl mx-auto my-8 bg-white rounded-xl shadow-2xl p-6"><div class="flex items-center justify-between mb-4"><h2 class="text-xl font-bold">Cập nhật hồ sơ bệnh nhân</h2><button id="care-close" type="button" class="text-xl" aria-label="Đóng">×</button></div><form id="care-patient-form" class="grid grid-cols-1 sm:grid-cols-2 gap-4"><div class="sm:col-span-2 flex justify-end gap-3"><button type="button" id="care-cancel" class="px-4 py-2 rounded-lg border">Hủy</button><button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 text-white">Lưu thay đổi</button></div></form></div>`;
  const form = $('#care-patient-form', modal);
  const actions = form.firstElementChild;
  fields.forEach(([key, label, kind]) => {
    const wrap = document.createElement('label');
    wrap.className = 'flex flex-col gap-1 text-xs font-semibold text-gray-700' + (kind === 'textarea' ? ' sm:col-span-2' : '');
    const title = document.createElement('span'); title.textContent = label; wrap.append(title);
    const control = kind === 'textarea' ? document.createElement('textarea') : kind === 'select' ? document.createElement('select') : document.createElement('input');
    if (kind !== 'textarea' && kind !== 'select') control.type = kind;
    if (kind === 'select') ['', 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].forEach(v => { const option = document.createElement('option'); option.value = v; option.textContent = v || 'Chưa xác định'; control.append(option); });
    control.name = key; control.className = 'px-3 py-2 rounded-lg border border-gray-200 w-full text-sm';
    control.maxLength = kind === 'textarea' ? 500 : 200;
    wrap.append(control); form.insertBefore(wrap, actions);
  });
  document.body.append(modal);
  const open = () => { fields.forEach(([key]) => { form.elements.namedItem(key).value = profile[key] || ''; }); modal.classList.remove('hidden'); };
  const close = () => modal.classList.add('hidden');
  $('#care-close', modal).addEventListener('click', close);
  $('#care-cancel', modal).addEventListener('click', close);
  modal.addEventListener('click', e => { if (e.target === modal) close(); });
  all('button').filter(e => /Chỉnh sửa thông tin|Chỉnh sửa|Cập nhật/.test(e.textContent)).forEach(btn => btn.addEventListener('click', open));
  all('a').filter(a=>a.textContent.includes('Lịch hẹn của tôi')).forEach(a=>a.href='/patient/appointments');
  all('a').filter(a=>a.textContent.trim()==='Bác sĩ').forEach(a=>a.href='/patient/booking');
  all('a').filter(a=>a.textContent.includes('Đặt lịch khám')).forEach(a=>a.href='/patient/booking');
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const payload = Object.fromEntries(new FormData(form).entries());
    const submit = form.querySelector('[type="submit"]'); submit.disabled = true;
    try {
      const r = await fetch(API, { method: 'PUT', headers: { 'Content-Type': 'application/json', [window.CARE_CSRF.header]: window.CARE_CSRF.token }, credentials: 'same-origin', body: JSON.stringify(payload) });
      const data = await r.json(); if (!r.ok) throw new Error(data.error || 'Không thể cập nhật hồ sơ');
      identity = data; profile = data.fields || {}; render(); close(); show('Thành công', 'Đã lưu dữ liệu hồ sơ vào cơ sở dữ liệu.');
    } catch (err) { show('Không thể lưu', err.message); } finally { submit.disabled = false; }
  });

  function openPasswordEditor() {
    const dialog=document.createElement('div');dialog.className='fixed inset-0 bg-black/40 z-[120] p-4 flex items-center justify-center';
    dialog.innerHTML=`<form class="bg-white rounded-xl shadow-xl p-6 max-w-md w-full space-y-3"><h2 class="text-lg font-bold">Đổi mật khẩu tài khoản</h2><label class="block text-xs">Mật khẩu hiện tại<input class="block w-full border rounded-lg p-2 mt-1" name="oldPassword" type="password" required autocomplete="current-password"></label><label class="block text-xs">Mật khẩu mới<input class="block w-full border rounded-lg p-2 mt-1" name="newPassword" type="password" required autocomplete="new-password"></label><label class="block text-xs">Nhập lại mật khẩu mới<input class="block w-full border rounded-lg p-2 mt-1" name="confirmPassword" type="password" required autocomplete="new-password"></label><div class="flex gap-3 justify-end"><button type="button" id="pw-cancel" class="px-4 py-2 border rounded-lg">Hủy</button><button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded-lg">Đổi mật khẩu</button></div></form>`;
    document.body.append(dialog);dialog.querySelector('#pw-cancel').onclick=()=>dialog.remove();
    dialog.querySelector('form').addEventListener('submit',async e=>{
      e.preventDefault();const form=e.currentTarget;
      try {const body=Object.fromEntries(new FormData(form).entries());
        const r=await fetch('/patient/api/change-password',{method:'POST',credentials:'same-origin',headers:{'Content-Type':'application/json',[window.CARE_CSRF.header]:window.CARE_CSRF.token},body:JSON.stringify(body)});
        const data=await r.json();if(!r.ok)throw Error(data.error||'Không thể đổi mật khẩu');
        dialog.remove();show('Thành công',data.message);
      }catch(err){show('Lỗi',err.message);}
    });
  }
  all('a[href="#cai-dat-tai-khoan"]').forEach(a=>a.addEventListener('click',e=>{e.preventDefault();openPasswordEditor();}));

  async function init() {
    try { const res = await fetch(API, { credentials: 'same-origin' }); if (!res.ok) throw new Error('Không thể đọc hồ sơ (' + res.status + ')'); identity = await res.json(); profile = identity.fields || {}; render(); }
    catch (err) { show('Lỗi', err.message); }
  }
  init();
})();
