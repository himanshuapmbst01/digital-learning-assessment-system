var splide = new Splide( '.splide', {
    type   : 'loop',
    perPage: 3,
    perMove: 1,
    autoScroll:true,
    breakpoints: {
      992: {
        perPage: 2,
      },
      767: {
        perPage: 1,
      },
    },
  } );
  
  splide.mount();

var splide1 = new Splide( '.splide1', {
  type   : 'loop',
  perPage: 3,
  perMove: 1,
  autoScroll:true,
  
    breakpoints: {
      1040: {
        perPage: 2,
      },
      767: {
        perPage: 1,
      },
    },
  
} );

splide1.mount();

var splide2 = new Splide( '.splide2' );
splide2.mount();

var today = new Date();
    document.getElementById("p1").innerHTML = today;
