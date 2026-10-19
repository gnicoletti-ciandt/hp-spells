const test = require("node:test");
const assert = require("node:assert/strict");
const SpellLogic = require("../public/spellLogic.js");

// UNIT tests: pure functions only, no DOM, no fetch, no browser.

test("resolveApiBase prefers the apiBase query string parameter", () => {
  assert.equal(SpellLogic.resolveApiBase("?apiBase=http://localhost:9999", "http://localhost:4100"), "http://localhost:9999");
});

test("resolveApiBase falls back to the global override when there is no query param", () => {
  assert.equal(SpellLogic.resolveApiBase("", "http://localhost:4321"), "http://localhost:4321");
});

test("resolveApiBase falls back to localhost:4100 when nothing else is provided", () => {
  assert.equal(SpellLogic.resolveApiBase("", undefined), "http://localhost:4100");
});

test("buildSpellRequestUrl encodes the spell name into the query string", () => {
  assert.equal(
    SpellLogic.buildSpellRequestUrl("http://localhost:4100", "Wingardium Leviosa"),
    "http://localhost:4100/api/spell?name=Wingardium%20Leviosa"
  );
});

test("buildSpellListUrl points at the spells list endpoint", () => {
  assert.equal(SpellLogic.buildSpellListUrl("http://localhost:4100"), "http://localhost:4100/api/spells");
});

test("isSameSpell ignores case and surrounding whitespace", () => {
  assert.equal(SpellLogic.isSameSpell(" lumos ", "Lumos"), true);
});

test("isSameSpell is false for different spells and tolerates undefined", () => {
  assert.equal(SpellLogic.isSameSpell("Lumos", "Nox"), false);
  assert.equal(SpellLogic.isSameSpell(undefined, "Nox"), false);
});

test("formatSpellName prefixes the spell name with a sparkle", () => {
  assert.equal(SpellLogic.formatSpellName("Lumos"), "\u2728 Lumos");
});

test("resolveErrorMessage prefers the error field from the response body", () => {
  assert.equal(SpellLogic.resolveErrorMessage({ error: "no spell found" }, 404), "no spell found");
});

test("resolveErrorMessage falls back to a generic message when the body has no error field", () => {
  assert.equal(SpellLogic.resolveErrorMessage({}, 502), "Request failed with status 502");
});

test("resolveErrorMessage falls back to a generic message when the body is undefined", () => {
  assert.equal(SpellLogic.resolveErrorMessage(undefined, 500), "Request failed with status 500");
});
