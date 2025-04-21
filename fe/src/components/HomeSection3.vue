<!-- Component cha -->
<script setup lang="ts">
import {
  Accordion,
  AccordionContent,
  AccordionHeader,
  AccordionPanel,
  Avatar,
  Badge,
} from 'primevue'
import SectionContainer from './SectionContainer.vue'
</script>

<template>
  <SectionContainer>
    <p class="text-center font-bold text-xl text-[#8c8c8c] uppercase">
      one <span class="font-bold text-[#9bca37]">learner</span>, one
      <span class="font-bold text-[#ec9035]">jorney</span>.
    </p>
    <p class="text-center font-bold text-2xl my-3">
      Chương trình học tập được thiết kế <br /><span class="text-[#ec9035] font-bold"
        >cá nhân hóa</span
      >
    </p>
    <p class="text-lg font-medium">
      Học viên học tập và phát triển qua những lộ trình cá nhân hóa, được thiết kế dựa trên nhu cầu,
      sở thích và khả năng của từng cá nhân, với mức học phí hợp lý.
    </p>
    <div class="card">
      <Accordion value="0" expandIcon="null" collapseIcon="null">
        <AccordionPanel value="0">
          <AccordionHeader>
            <span class="flex items-center gap-2 w-full">
              <Avatar
                image="https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png"
                shape="circle"
              />
              <span class="font-bold whitespace-nowrap">Amy Elsner</span>
            </span>
          </AccordionHeader>
          <AccordionContent>
            <p class="m-0">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
              incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
              exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
              dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
              Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
              mollit anim id est laborum.
            </p>
          </AccordionContent>
        </AccordionPanel>
        <AccordionPanel value="1">
          <AccordionHeader>
            <span class="flex items-center gap-2 w-full">
              <Avatar
                image="https://primefaces.org/cdn/primevue/images/avatar/onyamalimba.png"
                shape="circle"
              />
              <span class="font-bold whitespace-nowrap">Onyama Limba</span>
            </span>
          </AccordionHeader>
          <AccordionContent>
            <p class="m-0">
              Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque
              laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi
              architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas
              sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione
              voluptatem sequi nesciunt. Consectetur, adipisci velit, sed quia non numquam eius
              modi.
            </p>
          </AccordionContent>
        </AccordionPanel>
        <AccordionPanel value="2">
          <AccordionHeader>
            <span class="flex items-center gap-2 w-full">
              <Avatar
                image="https://primefaces.org/cdn/primevue/images/avatar/ionibowcher.png"
                shape="circle"
              />
              <span class="font-bold whitespace-nowrap">Ioni Bowcher</span>
            </span>
          </AccordionHeader>
          <AccordionContent>
            <p class="m-0">
              At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium
              voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint
              occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt
              mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et
              expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque
              nihil impedit quo minus.
            </p>
          </AccordionContent>
        </AccordionPanel>
      </Accordion>
      <img src="https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fprogram-4.c88ce151.webp&w=1080&q=75" alt="">
    </div>
  </SectionContainer>
</template>
