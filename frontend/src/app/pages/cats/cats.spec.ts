import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { CatsComponent } from './cats';
import { CatService } from '../../services/cat-service';
import { Cat } from '../../models/cat-model';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { provideRouter } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('CatsComponent', () => {
  let component: CatsComponent;
  let fixture: ComponentFixture<CatsComponent>;
  let catService: jasmine.SpyObj<CatService>;
  let mockActivatedRoute: any;

  const mockCats: Cat[] = [
    {
      id: 1,
      name: 'Luna',
      age: 2,
      gender: 'FEMALE',
      breedId: 'pers',
      breedName: 'Persian Mix',
      description: 'Sweet and gentle cat looking for a loving home',
      adoptionStatus: 'AVAILABLE',
      latitude: 53.124,
      longitude: 14.223,
      address: 'Dresden',
      imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
      createdAt: '2024-01-15T10:00:00Z',
      updatedAt: '2024-01-15T10:00:00Z'
    },
    {
      id: 2,
      name: 'Max',
      age: 5,
      gender: 'MALE',
      breedId: 'siam',
      breedName: 'Siamese',
      description: 'Playful and energetic cat, loves to chase toys',
      adoptionStatus: 'AVAILABLE',
      latitude: 53.124,
      longitude: 14.223,
      address: 'Dresden',
      imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
      createdAt: '2024-01-15T10:00:00Z',
      updatedAt: '2024-01-15T10:00:00Z'
    },
    {
      id: 3,
      name: 'Bella',
      age: 8,
      gender: 'FEMALE',
      breedId: 'mcoo',
      breedName: 'Maine Coon',
      description: 'Senior cat with lots of love to give',
      adoptionStatus: 'AVAILABLE',
      latitude: 53.124,
      longitude: 14.223,
      address: 'Leipzig',
      imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
      createdAt: '2024-01-15T10:00:00Z',
      updatedAt: '2024-01-15T10:00:00Z'
    },
    {
      id: 4,
      name: 'Charlie',
      age: 1,
      gender: 'MALE',
      breedId: 'pers',
      breedName: 'Persian Mix',
      description: 'Young and playful kitten looking for an active family',
      adoptionStatus: 'AVAILABLE',
      latitude: 53.124,
      longitude: 14.223,
      address: 'Munich',
      imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
      createdAt: '2024-01-15T10:00:00Z',
      updatedAt: '2024-01-15T10:00:00Z'
    }
  ];

  beforeEach(async () => {
    const catServiceSpy = jasmine.createSpyObj('CatService', ['getAllCats']);

    mockActivatedRoute = {
      queryParams: of({})
    };

    await TestBed.configureTestingModule({
      imports: [
        CatsComponent,
        NoopAnimationsModule
      ],
      providers: [
        provideRouter([]),
        { provide: CatService, useValue: catServiceSpy },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CatsComponent);
    component = fixture.componentInstance;
    catService = TestBed.inject(CatService) as jasmine.SpyObj<CatService>;

    catService.getAllCats.and.returnValue(of(mockCats));
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should start with loading = true', () => {
    expect(component.loading).toBe(true);
  });

  it('should start with empty arrays', () => {
    expect(component.cats).toEqual([]);
    expect(component.filteredCats).toEqual([]);
    expect(component.allCats).toEqual([]);
  });

  it('should have correct default pagination values', () => {
    expect(component.pageSize).toBe(12);
    expect(component.pageIndex).toBe(0);
    expect(component.totalCats).toBe(0);
  });

  it('should have empty currentFilters object', () => {
    expect(component.currentFilters).toEqual({});
  });

  it('should call loadAvailableCats when ngOnInit runs', () => {
    spyOn(component, 'loadAvailableCats');

    component.ngOnInit();

    expect(component.loadAvailableCats).toHaveBeenCalled();
  });

  it('should load cats successfully', () => {
    component.loadAvailableCats();

    expect(catService.getAllCats).toHaveBeenCalled();
    expect(component.allCats).toEqual(mockCats);
    expect(component.loading).toBe(false);
  });

  it('should filter only AVAILABLE cats', () => {
    const catsWithDifferentStatuses = [
      ...mockCats,
      {
        ...mockCats[0],
        id: 5,
        name: 'NotAvailable',
        adoptionStatus: 'ADOPTED'
      }
    ];
    catService.getAllCats.and.returnValue(of(catsWithDifferentStatuses as any));

    component.loadAvailableCats();

    expect(component.allCats.length).toBe(4);
    expect(component.allCats.every(cat => cat.adoptionStatus === 'AVAILABLE')).toBe(true);
  });

  it('should handle error when loading cats', () => {
    spyOn(console, 'error');
    catService.getAllCats.and.returnValue(throwError(() => new Error('Service error')));

    component.loadAvailableCats();

    expect(component.loading).toBe(false);
    expect(console.error).toHaveBeenCalledWith('Error loading cats:', jasmine.any(Error));
  });

  it('should use mock cats when service fails', () => {
    catService.getAllCats.and.returnValue(throwError(() => new Error('Service error')));

    component.loadAvailableCats();

    expect(component.allCats).toBeDefined();
    expect(component.allCats.length).toBeGreaterThan(0);
  });

  it('should apply filters correctly', () => {
    component.allCats = mockCats;
    component.currentFilters = { gender: 'FEMALE' };

    component.applyFilters();

    const femaleCats = mockCats.filter(cat => cat.gender === 'FEMALE');
    expect(component.filteredCats).toEqual(femaleCats);
    expect(component.totalCats).toBe(femaleCats.length);
  });

  it('should filter by breed name', () => {
    component.allCats = mockCats;
    component.currentFilters = { breed: 'Persian Mix' };

    component.applyFilters();

    const persianCats = mockCats.filter(cat => cat.breedName === 'Persian Mix');
    expect(component.filteredCats).toEqual(persianCats);
  });

  it('should filter by age range', () => {
    component.allCats = mockCats;
    component.currentFilters = { ageRange: '0-2' };

    component.applyFilters();

    const youngCats = mockCats.filter(cat => cat.age >= 0 && cat.age <= 2);
    expect(component.filteredCats).toEqual(youngCats);
  });

  it('should show all cats when no filters applied', () => {
    component.allCats = mockCats;
    component.currentFilters = {};

    component.applyFilters();

    expect(component.filteredCats).toEqual(mockCats);
    expect(component.totalCats).toBe(mockCats.length);
  });

  it('should reset pageIndex when onFiltersChanged called', () => {
    component.pageIndex = 5;
    spyOn(component, 'applyFilters');

    component.onFiltersChanged({ breed: 'Persian' });

    expect(component.pageIndex).toBe(0);
    expect(component.applyFilters).toHaveBeenCalled();
  });

  it('should update pagination when onPageChange called', () => {
    const pageEvent = { pageIndex: 2, pageSize: 24, length: 100 };

    component.onPageChange(pageEvent);

    expect(component.pageIndex).toBe(2);
    expect(component.pageSize).toBe(24);
  });

  it('should return correct pagedCats', () => {
    component.filteredCats = mockCats;
    component.pageSize = 2;
    component.pageIndex = 0;

    const result = component.pagedCats;

    expect(result.length).toBe(2);
    expect(result).toEqual(mockCats.slice(0, 2));
  });

  it('should clear filters correctly', () => {
    component.currentFilters = { breed: 'Persian', gender: 'MALE' };
    component.pageIndex = 3;
    spyOn(component, 'applyFilters');

    component.clearFilters();

    expect(component.currentFilters).toEqual({});
    expect(component.pageIndex).toBe(0);
    expect(component.applyFilters).toHaveBeenCalled();
  });
});
