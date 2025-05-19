<template>
    <div class="not-found">
        <h1>404</h1>
        <p>Oops! The page you are looking for does not exist.</p>
        <router-link to="/">Go back to Home</router-link>
    </div>
</template>

<script lang="ts">
export default {
    name: "NotFoundPage",
};
</script>

<style scoped>
.not-found {
    text-align: center;
    margin-top: 100px;
}

.not-found h1 {
    font-size: 6rem;
    color: #ff6b6b;
}

.not-found p {
    font-size: 1.5rem;
    margin: 20px 0;
}

.not-found a {
    color: #3498db;
    text-decoration: none;
    font-weight: bold;
}

.not-found a:hover {
    text-decoration: underline;
}
</style>