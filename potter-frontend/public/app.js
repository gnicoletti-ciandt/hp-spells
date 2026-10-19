const form = document.getElementById("spell-form");
const nameInput = document.getElementById("name-input");
const spellCard = document.getElementById("spell-card");
const spellName = document.getElementById("spell-name");
const spellDescription = document.getElementById("spell-description");
const errorMessage = document.getElementById("error-message");
const spellList = document.getElementById("spell-list");
const spellListStatus = document.getElementById("spell-list-status");

function apiBase() {
  return SpellLogic.resolveApiBase(window.location.search, window.__POTTER_SERVICE_URL__);
}

function highlightSelected(name) {
  for (const button of spellList.querySelectorAll("button")) {
    button.classList.toggle("selected", SpellLogic.isSameSpell(button.dataset.name, name));
  }
}

async function lookupSpell(name) {
  errorMessage.hidden = true;
  spellCard.hidden = true;

  const url = SpellLogic.buildSpellRequestUrl(apiBase(), name);

  let response;
  try {
    response = await fetch(url);
  } catch (networkError) {
    showError("Could not reach the spell service. Please try again.");
    return;
  }

  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    showError(SpellLogic.resolveErrorMessage(body, response.status));
    return;
  }

  const spell = await response.json();
  spellName.textContent = SpellLogic.formatSpellName(spell.name);
  spellDescription.textContent = spell.description;
  spellCard.hidden = false;
  highlightSelected(spell.name);
}

async function loadSpellList() {
  try {
    const response = await fetch(SpellLogic.buildSpellListUrl(apiBase()));
    if (!response.ok) {
      throw new Error(`status ${response.status}`);
    }
    const spells = await response.json();
    for (const spell of spells) {
      const item = document.createElement("li");
      const button = document.createElement("button");
      button.type = "button";
      button.textContent = spell.name;
      button.title = spell.description;
      button.dataset.name = spell.name;
      button.addEventListener("click", () => {
        nameInput.value = spell.name;
        lookupSpell(spell.name);
      });
      item.appendChild(button);
      spellList.appendChild(item);
    }
    spellListStatus.hidden = true;
    highlightSelected(nameInput.value);
  } catch (error) {
    spellListStatus.textContent = "Could not load the spell list.";
  }
}

function showError(message) {
  errorMessage.textContent = message;
  errorMessage.hidden = false;
}

form.addEventListener("submit", (event) => {
  event.preventDefault();
  lookupSpell(nameInput.value.trim());
});

loadSpellList();
lookupSpell(nameInput.value.trim());
