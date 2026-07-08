// clusterize
var clusterize;
window.addEventListener('DOMContentLoaded', () => {
    clusterize = new Clusterize({
        rows: [],
        scrollId: 'scrollArea',
        contentId: 'contentArea'
    });
});

let curScrollHeight = 0;

function appendMessage(content) {
    const rawJson = atob(content);
    const newdata = JSON.parse(rawJson);
    clusterize.append(newdata);
    window.scrollTo(0, document.body.scrollHeight);
}

function prependMessages(content) {
    const rawJson = atob(content);
    const newdata = JSON.parse(rawJson);
    clusterize.prepend(newdata);
    // // adjust scroll position
    let newHeight = document.body.scrollHeight
    window.scrollTo(0, (newHeight - curScrollHeight))
}

function scrollHandler() {
    curScrollHeight = document.body.scrollHeight
    if(window.scrollY <= 10) {
        chatHistoryProvider.loadMessageHistory();
    }
}

function startStreaming(content) {
    const container = document.getElementById("streaming");
    if(container) {
        container.innerHTML = atob(content)
    }
}

function stream(content) {
    const container = document.getElementById("stream-content");
    if(container) {
        container.innerHTML = atob(content)
        window.scrollTo(0, document.body.scrollHeight);
    }
}

function streamComplete() {
    const container = document.getElementById("streaming");
    if(container) {
        const loader = container.querySelector("#loader");
        if(loader) {
            loader.remove()
        }
        clusterize.append([container.innerHTML]);
        container.innerHTML = ""
    }
}

function setTheme(theme) {
    const htmlElement = document.documentElement;
    htmlElement.setAttribute("data-theme", theme);
}

const toolbar = document.getElementById('hover-toolbar');
const scrollArea = document.getElementById('scrollArea');
const contentArea = document.getElementById('contentArea');
let currentActiveRow = null;

// detect mouse hover and move toolbar within row
contentArea.addEventListener('mouseover', function(event) {
    const row = event.target.closest('.chat-message');

    // if not in row, or the same row, skip
    if (!row || row === currentActiveRow) return;

    currentActiveRow = row;

    // get data-id from row and write it to toolbar
    const rowId = row.getAttribute('data-id');
    toolbar.setAttribute('data-active-id', rowId);

    // move toolbar DOM element inside current row
    row.appendChild(toolbar);

    // show toolbar
    toolbar.style.display = 'flex';
});

// hide toolbar when mouse leaves row
contentArea.addEventListener('mouseout', function(event) {
    // if mouse moves from row to toolbar, do not hide it
    if (toolbar.contains(event.relatedTarget)) return;

    const leavingRow = event.target.closest('.chat-message');

    // hide if mouse leaves current row
    if (leavingRow === currentActiveRow) {
        hideToolbar();
    }
});

function hideToolbar() {
    if (!currentActiveRow) return;

    toolbar.style.display = 'none';
    // put toolbar back inside Clusterize range
    scrollArea.appendChild(toolbar);
    currentActiveRow = null;
}

// hide toolbar on scroll
scrollArea.addEventListener('scroll', function() {
    hideToolbar();
}, { passive: true }); // passive za bolje performanse skrolovanja


// toolbar buttons click handler
toolbar.addEventListener('click', function(event) {
    const button = event.target.closest('.tool-btn');
    if (!button) return;

    // read row id from toolbar
    const rowId = toolbar.getAttribute('data-active-id');
    const action = button.getAttribute('data-action');

    if (action === 'copy-md') {
        chatMsgTools.copyMarkdown(rowId);
    } else if (action === 'copy-html') {
        const row = document.querySelector(`[data-id="${rowId}"]`);
        if (row) {
            const contentElement = row.querySelector('.chat-content > div:nth-child(2)');
            if (contentElement) {
                chatMsgTools.copyHtml(contentElement.innerHTML);
            }
        }
    }
});