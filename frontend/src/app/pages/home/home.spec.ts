import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HomeComponent } from './home';
import { CatService } from '../../services/cat-service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { provideRouter } from '@angular/router';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let catService: jasmine.SpyObj<CatService>;

  beforeEach(async () => {
    const catServiceSpy = jasmine.createSpyObj('CatService', ['getAllCats', 'getCatBreeds']);

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideRouter([]),
        { provide: CatService, useValue: catServiceSpy }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    catService = TestBed.inject(CatService) as jasmine.SpyObj<CatService>;

    catService.getAllCats.and.returnValue(of([]));
    catService.getCatBreeds.and.returnValue(of([]));
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should start with loading = true', () => {
    expect(component.loading).toBe(true);
  });

  it('should start with empty featuredCats array', () => {
    expect(component.featuredCats).toEqual([]);
  });

  it('should start with empty popularBreeds array', () => {
    expect(component.popularBreeds).toEqual([]);
  });

  it('should have 3 success stories', () => {
    expect(component.successStories.length).toBe(3);
  });

  it('should have popularBreedsIds defined', () => {
    expect(component.popularBreedsIds).toBeDefined();
    expect(component.popularBreedsIds.length).toBeGreaterThan(0);
  });

  it('should call loadFeaturedCats when ngOnInit runs', () => {
    spyOn(component, 'loadFeaturedCats');

    component.ngOnInit();

    expect(component.loadFeaturedCats).toHaveBeenCalled();
  });

  it('should call loadPopularBreeds when ngOnInit runs', () => {
    spyOn(component, 'loadPopularBreeds');

    component.ngOnInit();

    expect(component.loadPopularBreeds).toHaveBeenCalled();
  });

  it('should set loading to false after loadFeaturedCats', () => {
    component.loadFeaturedCats();

    expect(component.loading).toBe(false);
  });

  it('should call CatService.getAllCats when loadFeaturedCats runs', () => {
    component.loadFeaturedCats();

    expect(catService.getAllCats).toHaveBeenCalled();
  });

  it('should call CatService.getCatBreeds when loadPopularBreeds runs', () => {
    component.loadPopularBreeds();

    expect(catService.getCatBreeds).toHaveBeenCalled();
  });

  it('should log message when onFiltersChanged is called', () => {
    spyOn(console, 'log');
    const testFilters = { breed: 'Persian' };

    component.onFiltersChanged(testFilters);

    expect(console.log).toHaveBeenCalledWith('Filters changed:', testFilters);
  });
});
