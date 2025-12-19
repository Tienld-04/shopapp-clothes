const createDialog = document.getElementById('create-dialog');
const updateDialog = document.getElementById('update-dialog');
const detailDialog = document.getElementById('detail-dialog');

const createForm = document.getElementById('create-product-form');
const updateForm = document.getElementById('update-product-form');
const detailForm = document.getElementById('detail-form');

const btnOpenCreate = document.getElementById('btn-open-create');
const openDetailFromUpdate = document.getElementById('open-detail-dialog');

const detailList = document.getElementById('detail-list');
const detailTitle = document.getElementById('detail-title');
const detailSubtitle = document.getElementById('detail-subtitle');
const detailProductIdInput = document.getElementById('detail-product-id');

const closeButtons = document.querySelectorAll('[data-close]');
const editButtons = document.querySelectorAll('[data-action="edit"]');
const detailButtons = document.querySelectorAll('[data-action="details"]');

const createControls = getControls(createForm, ['name', 'price', 'material', 'brand', 'description', 'categoryId', 'images']);
const updateControls = getControls(updateForm, ['id', 'name', 'price', 'material', 'brand', 'description', 'categoryId']);
const detailControls = getControls(detailForm, ['color', 'size', 'quantity', 'priceAdjustment', 'productId']);

const createImageInput = document.getElementById('create-images');
const createImagePreview = document.getElementById('create-image-preview');
const triggerUploadButton = document.getElementById('btn-trigger-upload');

const updateImageInput = document.getElementById('update-images');
const updateImagePreview = document.getElementById('update-image-preview');
const updateCurrentImages = document.getElementById('update-current-images');
const triggerUpdateUploadButton = document.getElementById('btn-trigger-update-upload');

const pendingCreateImages = [];
const pendingUpdateImages = [];
let currentProductImages = [];

btnOpenCreate?.addEventListener('click', () => openDialog(createDialog));

closeButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        const dialog = btn.closest('dialog');
        dialog?.close();
    });
});

editButtons.forEach(button => {
    button.addEventListener('click', () => {
        const dataset = button.dataset;
        if (!updateForm) return;
        updateControls.id && (updateControls.id.value = dataset.id || '');
        updateControls.name && (updateControls.name.value = dataset.name || '');
        updateControls.price && (updateControls.price.value = dataset.price || '');
        updateControls.material && (updateControls.material.value = dataset.material || '');
        updateControls.brand && (updateControls.brand.value = dataset.brand || '');
        updateControls.description && (updateControls.description.value = dataset.description || '');
        selectCategoryByName(updateControls.categoryId, dataset.categoryName);
        pendingUpdateImages.length = 0;
        loadProductImages(dataset.id);
        openDialog(updateDialog);
    });
});

detailButtons.forEach(button => {
    button.addEventListener('click', () => {
        const productId = button.dataset.id;
        const productName = button.dataset.name;
        openProductDetails(productId, productName);
    });
});

openDetailFromUpdate?.addEventListener('click', () => {
    const productId = updateControls.id?.value;
    if (!productId) {
        alert('Vui lòng chọn sản phẩm trước.');
        return;
    }
    const productName = updateControls.name?.value;
    openProductDetails(productId, productName);
});

triggerUploadButton?.addEventListener('click', () => {
    createImageInput?.click();
});

triggerUpdateUploadButton?.addEventListener('click', () => {
    updateImageInput?.click();
});

createImageInput?.addEventListener('change', () => {
    if (!createImageInput.files?.length) return;
    Array.from(createImageInput.files).forEach(file => pendingCreateImages.push(file));
    createImageInput.value = '';
    renderCreateImagePreview();
});

updateImageInput?.addEventListener('change', () => {
    if (!updateImageInput.files?.length) return;
    Array.from(updateImageInput.files).forEach(file => pendingUpdateImages.push(file));
    updateImageInput.value = '';
    renderUpdateImagePreview();
});

createForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    await submitProductForm(createControls, '/products', 'POST', 'Thêm sản phẩm thành công', {
        multipart: true,
        files: pendingCreateImages
    });
});

updateForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const productId = updateControls.id?.value;
    await submitProductForm(updateControls, `/products/${productId}`, 'PUT', 'Cập nhật sản phẩm thành công', {
        multipart: true,
        files: pendingUpdateImages
    });
});

detailForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const productId = detailProductIdInput.value;
    const quantityValue = detailControls.quantity?.value || '0';
    const priceAdjustValue = detailControls.priceAdjustment?.value || '';
    const payload = {
        color: detailControls.color?.value,
        size: detailControls.size?.value,
        quantity: Number(quantityValue),
        priceAdjustment: priceAdjustValue ? Number(priceAdjustValue) : null
    };
    try {
        await requestJSON(`/products/${productId}/details`, 'POST', payload);
        await loadDetails(productId);
        detailForm.reset();
        if (detailControls.productId) {
            detailControls.productId.value = productId;
        }
        detailProductIdInput.value = productId;
        alert('Thêm chi tiết sản phẩm thành công');
    } catch (error) {
        alert(error.message);
    }
});

async function submitProductForm(controls, url, method, successMessage, options = {}) {
    let payload;
    if (options.multipart) {
        const formData = new FormData();
        appendFormData(formData, 'name', controls.name);
        appendFormData(formData, 'price', controls.price);
        appendFormData(formData, 'material', controls.material);
        appendFormData(formData, 'brand', controls.brand);
        appendFormData(formData, 'description', controls.description);
        appendFormData(formData, 'categoryId', controls.categoryId);
        const files = options.files ?? Array.from(controls.images?.files || []);
        files.forEach(file => formData.append('images', file));
        payload = formData;
    } else {
        payload = {
            name: controls.name?.value,
            price: Number(controls.price?.value || 0),
            material: controls.material?.value,
            brand: controls.brand?.value,
            description: controls.description?.value,
            categoryId: Number(controls.categoryId?.value || 0)
        };
    }
    try {
        await requestJSON(url, method, payload);
        alert(successMessage);
        window.location.reload();
    } catch (error) {
        alert(error.message);
    }
}

function selectCategoryByName(selectElement, categoryName) {
    if (!selectElement || !categoryName) return;
    const options = Array.from(selectElement.options);
    const match = options.find(option => option.text.trim() === categoryName.trim());
    if (match) {
        selectElement.value = match.value;
    } else {
        selectElement.selectedIndex = 0;
    }
}

function openDialog(dialog) {
    if (dialog && typeof dialog.showModal === 'function') {
        dialog.showModal();
    }
}

async function openProductDetails(productId, productName) {
    if (!productId) return;
    detailProductIdInput.value = productId;
    detailTitle.textContent = `Chi tiết sản phẩm #${productId}`;
    detailSubtitle.textContent = productName || '';
    await loadDetails(productId);
    openDialog(detailDialog);
}

async function loadDetails(productId) {
    try {
        const details = await requestJSON(`/products/${productId}/details`);
        renderDetailList(details);
    } catch (error) {
        detailList.innerHTML = `<p class="empty-state">${error.message}</p>`;
    }
}

function renderDetailList(details = []) {
    if (!detailList) return;
    if (!details.length) {
        detailList.innerHTML = '<p class="empty-state">Chưa có chi tiết nào.</p>';
        return;
    }
    detailList.innerHTML = '';
    details.forEach(detail => {
        const row = document.createElement('div');
        row.className = 'detail-item';
        row.innerHTML = `
            <span>Màu: <strong>${detail.color}</strong></span>
            <span>Size: <strong>${detail.size}</strong></span>
            <span>SL: <strong>${detail.quantity}</strong></span>
            <span>Phụ thu: <strong>${detail.priceAdjustment ?? 0}</strong></span>
        `;
        detailList.appendChild(row);
    });
}

async function requestJSON(url, method = 'GET', payload) {
    const options = { method };
    if (payload !== undefined) {
        if (payload instanceof FormData) {
            options.body = payload;
        } else {
            options.headers = { 'Content-Type': 'application/json' };
            options.body = JSON.stringify(payload);
        }
    }
    const response = await fetch(url, options);
    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || 'Đã xảy ra lỗi');
    }
    if (response.status === 204) {
        return null;
    }
    const contentType = response.headers.get('Content-Type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    }
    return null;
}

function getControls(form, names = []) {
    const map = {};
    if (!form) return map;
    names.forEach(name => {
        map[name] = form.elements.namedItem(name);
    });
    return map;
}

function appendFormData(formData, field, control) {
    if (!control) return;
    formData.append(field, control.value ?? '');
}

function renderImagePreview() {
    if (!createImagePreview) return;
    if (!pendingCreateImages.length) {
        createImagePreview.innerHTML = '<p class="empty-state">Chưa chọn ảnh nào.</p>';
        return;
    }
    createImagePreview.innerHTML = '';
    pendingCreateImages.forEach((file, index) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'image-preview__item';

        const remove = document.createElement('button');
        remove.type = 'button';
        remove.className = 'image-preview__remove';
        remove.textContent = '−';
        remove.addEventListener('click', () => {
            pendingCreateImages.splice(index, 1);
            renderImagePreview();
        });

        const img = document.createElement('img');
        const objectUrl = URL.createObjectURL(file);
        img.src = objectUrl;
        img.alt = file.name;
        img.onload = () => URL.revokeObjectURL(objectUrl);

        const caption = document.createElement('span');
        caption.textContent = file.name;

        wrapper.appendChild(remove);
        wrapper.appendChild(img);
        wrapper.appendChild(caption);
        createImagePreview.appendChild(wrapper);
    });
}

async function loadProductImages(productId) {
    try {
        const productData = await requestJSON(`/products`);
        const product = productData.find(p => p.id == productId);
        currentProductImages = product?.images || [];
        renderUpdateCurrentImages();
    } catch (error) {
        currentProductImages = [];
        renderUpdateCurrentImages();
    }
}

function renderUpdateCurrentImages() {
    if (!updateCurrentImages) return;
    if (!currentProductImages.length) {
        updateCurrentImages.innerHTML = '<p class="empty-state">Chưa có ảnh nào.</p>';
        return;
    }
    updateCurrentImages.innerHTML = '';
    currentProductImages.forEach((image, index) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'image-preview__item';

        const remove = document.createElement('button');
        remove.type = 'button';
        remove.className = 'image-preview__remove';
        remove.textContent = '−';
        remove.addEventListener('click', async (e) => {
            e.preventDefault();
            try {
                await requestJSON(`/products/images/${image.id}`, 'DELETE');
                currentProductImages.splice(index, 1);
                renderUpdateCurrentImages();
                alert('Xóa ảnh thành công');
            } catch (error) {
                alert('Lỗi khi xóa ảnh: ' + error.message);
            }
        });

        const img = document.createElement('img');
        img.src = image.imageUrl;
        img.alt = 'Product image ' + (index + 1);

        wrapper.appendChild(remove);
        wrapper.appendChild(img);
        updateCurrentImages.appendChild(wrapper);
    });
}

function renderUpdateImagePreview() {
    if (!updateImagePreview) return;
    if (!pendingUpdateImages.length) {
        updateImagePreview.innerHTML = '<p class="empty-state">Chưa chọn ảnh mới.</p>';
        return;
    }
    updateImagePreview.innerHTML = '';
    pendingUpdateImages.forEach((file, index) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'image-preview__item';

        const remove = document.createElement('button');
        remove.type = 'button';
        remove.className = 'image-preview__remove';
        remove.textContent = '−';
        remove.addEventListener('click', () => {
            pendingUpdateImages.splice(index, 1);
            renderUpdateImagePreview();
        });

        const img = document.createElement('img');
        const objectUrl = URL.createObjectURL(file);
        img.src = objectUrl;
        img.alt = file.name;
        img.onload = () => URL.revokeObjectURL(objectUrl);

        const caption = document.createElement('span');
        caption.textContent = file.name;

        wrapper.appendChild(remove);
        wrapper.appendChild(img);
        wrapper.appendChild(caption);
        updateImagePreview.appendChild(wrapper);
    });
}

function renderCreateImagePreview() {
    renderImagePreview();
}