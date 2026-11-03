document.querySelectorAll('.filterable').forEach(function (root) {
  var cat = '', q = '';
  function apply() {
    root.querySelectorAll('.grid-items .item').forEach(function (it) {
      var okCat = !cat || it.dataset.cat === cat;
      var okName = !q || it.dataset.name.indexOf(q) !== -1;
      it.style.display = (okCat && okName) ? '' : 'none';
    });
  }
  root.querySelectorAll('.tab').forEach(function (tab) {
    tab.addEventListener('click', function () {
      root.querySelectorAll('.tab').forEach(function (t) { t.classList.remove('on'); });
      tab.classList.add('on');
      cat = tab.dataset.cat;
      apply();
    });
  });
  root.querySelector('.search').addEventListener('input', function (e) {
    q = e.target.value.toLowerCase();
    apply();
  });
});
