/**
 * Pure, DOM-free logic extracted out of app.js so it can be unit tested
 * directly with Node's built-in test runner. Loaded as a plain <script> in
 * index.html (no import/export), and required from the tests via
 * `module.exports`.
 */
(function (root) {
  function resolveApiBase(searchString, globalOverride, fallback) {
    const params = new URLSearchParams(searchString || "");
    return params.get("apiBase") || globalOverride || fallback || "http://localhost:4100";
  }

  function buildSpellRequestUrl(apiBase, name) {
    return `${apiBase}/api/spell?name=${encodeURIComponent(name)}`;
  }

  function buildSpellListUrl(apiBase) {
    return `${apiBase}/api/spells`;
  }

  function formatSpellName(name) {
    return `\u2728 ${name}`;
  }

  function isSameSpell(a, b) {
    return String(a || "").trim().toLowerCase() === String(b || "").trim().toLowerCase();
  }

  function resolveErrorMessage(responseBody, status) {
    if (responseBody && typeof responseBody.error === "string" && responseBody.error.length > 0) {
      return responseBody.error;
    }
    return `Request failed with status ${status}`;
  }

  const SpellLogic = {
    resolveApiBase,
    buildSpellRequestUrl,
    buildSpellListUrl,
    formatSpellName,
    isSameSpell,
    resolveErrorMessage,
  };

  if (typeof module !== "undefined" && module.exports) {
    module.exports = SpellLogic;
  } else {
    root.SpellLogic = SpellLogic;
  }
})(typeof window !== "undefined" ? window : globalThis);
