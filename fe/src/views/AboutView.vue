<script setup lang="ts">
import { Button, ColorPicker, ConfirmDialog, DatePicker, Drawer, FloatLabel, InputMask, InputText, Knob } from 'primevue'
import { ref } from 'vue'
const value = ref(12)
const visible = ref(false)
</script>

<template>
  <div class="about bg-about-banner bg-pattern opacity-30 rotate-3 skew-x-12">
    <h1>This is an about page</h1>
    <div class="card flex justify-center">
    <Drawer v-model:visible="visible" header="Drawer">
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
    </Drawer>
    <Button icon="pi pi-arrow-right" @click="visible = true" >Click</Button>
</div>
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }

  .bg-about-banner {
    background-image: linear-gradient(#d4d6ff 1.9000000000000001px, transparent 0), linear-gradient(90deg, #d4d6ff 1.9000000000000001px, #fbfcff 0);
}

.bg-pattern {
    background-size: 50px 50px;
}

.opacity-30 {
    opacity: .3;
}

.skew-x-12 {
    --tw-skew-x: skewX(-30deg);
    transform: var(--tw-rotate-x) var(--tw-rotate-y) var(--tw-rotate-z) var(--tw-skew-x) var(--tw-skew-y);
}
.rotate-3 {
    rotate: 30deg;
}
@property --tw-rotate-x {
    syntax: "*";
    inherits: false;
    initial-value: rotateX(0);
}
@property --tw-rotate-y {
    syntax: "*";
    inherits: false;
    initial-value: rotateY(0);
}
@property --tw-rotate-z {
    syntax: "*";
    inherits: false;
    initial-value: rotateZ(0);
}
@property --tw-skew-x {
    syntax: "*";
    inherits: false;
    initial-value: skewX(0);
}
@property --tw-skew-y {
    syntax: "*";
    inherits: false;
    initial-value: skewY(0);
}

}
</style>
