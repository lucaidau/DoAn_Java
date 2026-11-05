'use strict';
(() => {
  const API = '/doctor/api/profile';
  let identity = {};
  let state = {};
  const $ = (s, root=document) => root.querySelector(s);
  const all = (s, root=document) => [...root.querySelectorAll(s)];
  const sections = all('main h3').map(h => h.closest('.bg-white')).filter(Boolean);
  const section = (title) => sections.find(e => e.querySelector('h3')?.textContent.includes(title));
  const identityBlock = section('Thông tin Cá nhân & Định danh');
  const bioBlock = section('Tiểu Sử & Năng Lực');
  const degreeBlock = section('Học Vấn & Chứng Chỉ');
  const workplaceBlock = section('Cơ Sở Công Tác');
  const feeBlock = section('Biểu Phí');
  const scheduleBlock = section('Lịch Khám Tuần');
  const rulesBlock = section('Cơ Chế Điều Hành');
  const idControls = identityBlock ? all('input,select', identityBlock) : [];
  const checks = (block) => block ? all('input[type="checkbox"]', block) : [];
  const fieldKeys = ['displayName','degree','specialty','contactEmail','contactPhone','licenseNumber'];
  const index = [0,1,2,3,4,5];
  const boolFields = ['healthInsurance', 'videoEnabled', 'weekdayMorning', 'weekdayAfternoon', 'saturdayMorning', 'saturdayAfternoon', 'acceptingPatients', 'autoApprove', 'emergencySms'];
  const toast = (message, bad=false) => { const div=document.createElement('div'); div.className='fixed z-[100] bottom-6 right-6 bg-white border rounded-xl shadow-xl p-4 text-sm '+(bad?'text-red-700':'text-green-700'); div.setAttribute('role','alert'); div.textContent=message; document.body.append(div); setTimeout(()=>div.remove(),5000); };
  const set = (el, value) => { if (el && value != null) el.value = value; };
  const text = (el, value) => { if (el && value != null) el.textContent = value; };
  const serialize = () => {
    const data = {...state};
    fieldKeys.forEach((key, i) => { data[key] = idControls[i]?.value.trim() || ''; });
    data.introduction = bioBlock?.querySelector('textarea')?.value || '';
    data.tags = tags.join('|');
    data.educationItems = JSON.stringify(educations);
    const feeChecks = checks(feeBlock), scheduleChecks = checks(scheduleBlock), ruleChecks = checks(rulesBlock);
    data.healthInsurance = !!feeChecks[0]?.checked;
    data.videoEnabled = !!feeChecks[1]?.checked;
    ['weekdayMorning','weekdayAfternoon','saturdayMorning','saturdayAfternoon'].forEach((k,i)=>data[k]=!!scheduleChecks[i]?.checked);
    ['acceptingPatients','autoApprove','emergencySms'].forEach((k,i)=>data[k]=!!ruleChecks[i]?.checked);
    const duration = scheduleBlock?.querySelector('select');
    data.duration = duration ? parseInt(duration.value,10) : 30;
    data.maxPatients = Number(scheduleBlock?.querySelector('input[type="number"]')?.value || 0);
    data.consultFee = Number(state.consultFee ?? 300000);
    data.videoFee = Number(state.videoFee ?? 250000);
    return data;
  };
  let tags=[], educations=[];
  const tagRow = bioBlock?.querySelector('.flex.flex-wrap.gap-2');
  function renderTags() {
    if (!tagRow) return;
    tagRow.replaceChildren();
    tags.forEach((tag, i) => { const chip=document.createElement('span'); chip.className='px-2.5 py-1 bg-gray-100 rounded-lg text-xs text-gray-700 flex items-center gap-1.5'; const label=document.createElement('span'); label.textContent=tag; const del=document.createElement('button'); del.type='button'; del.textContent='×'; del.setAttribute('aria-label','Xóa '+tag); del.addEventListener('click',()=>{tags.splice(i,1);renderTags();});chip.append(label,del);tagRow.append(chip); });
    const btn=document.createElement('button'); btn.type='button'; btn.className='px-2.5 py-1 border border-dashed border-blue-400 text-blue-600 rounded-lg text-xs hover:bg-blue-50 font-medium'; btn.textContent='+ Thêm chuyên môn';btn.addEventListener('click',()=>{const v=prompt('Nhập từ khóa chuyên môn (tối đa 80 ký tự):');if(v&&v.trim().length<=80&&!tags.includes(v.trim())){tags.push(v.trim());renderTags();}});tagRow.append(btn);
  }
  const degreeList = degreeBlock?.querySelector('.space-y-3');
  function renderDegrees() {
    if (!degreeList) return;
    degreeList.replaceChildren();
    educations.forEach((v,i)=>{const outer=document.createElement('div');outer.className='p-3 bg-gray-50/80 rounded-xl border border-gray-100 flex items-start justify-between';const label=document.createElement('div');label.className='text-xs text-gray-700';label.textContent=v;const controls=document.createElement('div');controls.className='flex gap-2';const edit=document.createElement('button');edit.type='button';edit.textContent='Sửa';edit.className='text-blue-600 text-xs';edit.addEventListener('click',()=>{const newV=prompt('Sửa bằng cấp/chứng chỉ',v);if(newV&&newV.trim().length<=250){educations[i]=newV.trim();renderDegrees();}});const del=document.createElement('button');del.type='button';del.textContent='Xóa';del.className='text-red-600 text-xs';del.addEventListener('click',()=>{if(confirm('Xóa bằng cấp này?')){educations.splice(i,1);renderDegrees();}});controls.append(edit,del);outer.append(label,controls);degreeList.append(outer);});
  }
  const addDegree=degreeBlock && all('button',degreeBlock).find(e=>e.textContent.includes('Thêm bằng cấp'));
  addDegree?.addEventListener('click',()=>{const input=prompt('Nhập tên bằng cấp, trường và năm cấp (tối đa 250 ký tự):');if(input&&input.trim().length<=250){educations.push(input.trim());renderDegrees();}});
  const updateHeader = () => {
    const current=state.displayName || identity.fullName || 'Bác sĩ';
    const headers=all('h2,p,span').filter(e=>e.children.length===0 && e.textContent.trim()==='BS. CKII Nguyễn Văn A');
    headers.forEach(el=>text(el,current));
    const spec=all('p').find(e=>e.textContent.includes('Khoa Tim Mạch Can Thiệp —'));
    if (spec) spec.textContent=(state.specialty||'Chưa cập nhật chuyên khoa')+' — '+(state.hospital||'Chưa cập nhật bệnh viện');
  };
  function render() {
    fieldKeys.forEach((k,i)=>set(idControls[i], state[k] ?? (k==='displayName'?identity.fullName: k==='contactEmail'?identity.email:k==='contactPhone'?identity.phone:'')));
    set(bioBlock?.querySelector('textarea'),state.introduction||'');
    tags=state.tags?state.tags.split('|').filter(Boolean):[];renderTags();
    try {educations=JSON.parse(state.educationItems||'[]');if(!Array.isArray(educations))educations=[];}catch{educations=[];}renderDegrees();
    const feeChecks=checks(feeBlock), scheduleChecks=checks(scheduleBlock), ruleChecks=checks(rulesBlock);
    ['healthInsurance','videoEnabled'].forEach((k,i)=>{if(feeChecks[i])feeChecks[i].checked=state[k]===true;});
    ['weekdayMorning','weekdayAfternoon','saturdayMorning','saturdayAfternoon'].forEach((k,i)=>{if(scheduleChecks[i]&&!scheduleChecks[i].disabled)scheduleChecks[i].checked=state[k]===true;});
    ['acceptingPatients','autoApprove','emergencySms'].forEach((k,i)=>{if(ruleChecks[i])ruleChecks[i].checked=state[k]===true;});
    const s=scheduleBlock?.querySelector('select');if(s){const d=Number(state.duration??30);all('option',s).forEach((option,i)=>option.value=[30,20,45][i]);s.value=String(d);}
    set(scheduleBlock?.querySelector('input[type="number"]'),state.maxPatients??10);
    updateHeader();
    const avatar=document.querySelector('img[alt="Doctor"]');
    if(avatar&&state.avatarData)avatar.src=state.avatarData;
    if(workplaceBlock){const ps=all('p',workplaceBlock);const hospital=ps.find(e=>e.className.includes('font-bold')&&e.textContent.includes('Bệnh viện'));if(hospital)text(hospital,state.hospital||'Chưa cập nhật bệnh viện'); const address=ps.find(e=>e.textContent.includes('215 Hồng Bàng'));if(address)text(address,state.workAddress||'Chưa cập nhật địa chỉ');}
    if(feeBlock){const prices=all('span.text-lg.font-bold',feeBlock);if(prices[0])prices[0].firstChild.textContent=Number(state.consultFee??300000).toLocaleString('vi-VN')+'đ ';if(prices[1])prices[1].firstChild.textContent=Number(state.videoFee??250000).toLocaleString('vi-VN')+'đ ';}
  }
  function editSimple(key,label,old, max=250) {const v=prompt(label,old||''); if(v===null) return; if(v.length>max){toast('Vượt quá '+max+' ký tự',true);return;}state[key]=v.trim();render();}
  const gpsBtn=workplaceBlock&&all('button',workplaceBlock).find(e=>e.textContent.includes('GPS'));
  gpsBtn?.addEventListener('click',()=>{toast('Không có quyền đồng bộ GPS tự động. Hãy nhập địa chỉ cơ sở công tác.'); editSimple('workAddress','Nhập địa chỉ cơ sở công tác:',state.workAddress);});
  const workplaceTitle=workplaceBlock?.querySelector('h3');workplaceTitle?.addEventListener('dblclick',()=>editSimple('hospital','Tên bệnh viện:',state.hospital));
  const clinicText=workplaceBlock&&all('span',workplaceBlock).find(e=>e.textContent.includes('Phòng khám:'));
  clinicText?.addEventListener('click',()=>editSimple('workplace','Số phòng khám:',state.workplace));
  const fees=feeBlock?.querySelectorAll('span.text-lg.font-bold');
  fees?.forEach((el,i)=>el.addEventListener('click',()=>{const key=i?'videoFee':'consultFee';const v=prompt('Giá khám (VND, số nguyên):',state[key]??(i?250000:300000));if(v!==null){if(!/^\d{1,9}$/.test(v))toast('Giá phải là số nguyên không âm',true);else{state[key]=Number(v);render();}}}));
  const photoButton=all('button').find(b=>b.querySelector('i.fa-camera'));
  photoButton?.addEventListener('click',()=>{
    const picker=document.createElement('input');picker.type='file';picker.accept='image/png,image/jpeg,image/webp';
    picker.addEventListener('change',()=>{
      const file=picker.files?.[0];if(!file)return;
      if(file.size>250000){toast('Ảnh tối đa 250 KB.',true);return;}
      const reader=new FileReader();reader.onload=()=>{state.avatarData=String(reader.result);render();toast('Đã chọn ảnh. Nhấn Lưu thay đổi để ghi vào cơ sở dữ liệu.');};reader.readAsDataURL(file);
    });picker.click();
  });
  all('a').filter(a=>a.textContent.includes('Trang chủ & Lịch hẹn')).forEach(a=>a.href='/doctor/appointments');
  const logoutLink=document.querySelector('a[href="/logout"]');
  logoutLink?.addEventListener('click',async e=>{
    e.preventDefault();
    try{await fetch('/logout',{method:'POST',credentials:'same-origin',headers:{[window.CARE_CSRF.header]:window.CARE_CSRF.token}});location.href='/login?logout';}
    catch{toast('Đăng xuất không thành công',true);}
  });
  const save=async()=>{
    const payload=serialize();
    try {const r=await fetch(API,{method:'PUT',credentials:'same-origin',headers:{'Content-Type':'application/json',[window.CARE_CSRF.header]:window.CARE_CSRF.token},body:JSON.stringify(payload)});const data=await r.json();if(!r.ok)throw Error(data.error||'Lưu thất bại');identity=data;state=data.fields||{};render();toast('Đã lưu thông tin bác sĩ thành công.');}
    catch(e){toast(e.message,true);}
  };
  all('button').filter(e=>/Lưu thay đổi|Lưu Toàn Bộ Cấu Hình/.test(e.textContent)).forEach(button=>button.addEventListener('click',save));
  async function init(){try{const r=await fetch(API,{credentials:'same-origin'});if(!r.ok)throw Error('Lỗi truy xuất hồ sơ: HTTP '+r.status);identity=await r.json();state=identity.fields||{};render();}catch(e){toast(e.message,true);}}
  init();
})();
