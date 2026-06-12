import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  ArrowRight,
  Award,
  BookOpen,
  ChevronRight,
  Code,
  LucideAngularModule,
  Quote,
  Star,
  Target,
  Users,
  Zap,
} from 'lucide-angular';
import * as THREE from 'three';
import { LogoComponent } from '../../../../shared/logo/logo.component';

@Component({
  imports: [RouterLink, LogoComponent, LucideAngularModule],
  templateUrl: './landing.page.html',
})
export class LandingPage implements AfterViewInit, OnDestroy {
  @ViewChild('threeContainer', { static: true })
  private readonly threeContainerRef!: ElementRef<HTMLDivElement>;

  readonly iconArrowRight = ArrowRight;
  readonly iconAward = Award;
  readonly iconBookOpen = BookOpen;
  readonly iconChevronRight = ChevronRight;
  readonly iconCode = Code;
  readonly iconQuote = Quote;
  readonly iconStar = Star;
  readonly iconTarget = Target;
  readonly iconUsers = Users;
  readonly iconZap = Zap;
  readonly currentYear = new Date().getFullYear();

  private renderer?: THREE.WebGLRenderer;
  private frame = 0;
  private resizeObserver?: ResizeObserver;
  private mouseMoveHandler?: (event: MouseEvent) => void;
  private disposeScene?: () => void;

  ngAfterViewInit(): void {
    const container = this.threeContainerRef.nativeElement;
    let width = container.clientWidth || 1;
    let height = container.clientHeight || 1;

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(60, width / height, 0.1, 100);
    camera.position.z = 18;

    const particleCount = 180;
    const geometry = new THREE.BufferGeometry();
    const positions = new Float32Array(particleCount * 3);
    const velocities: Array<{ x: number; y: number; z: number }> = [];

    for (let i = 0; i < particleCount; i++) {
      positions[i * 3] = (Math.random() - 0.5) * 32;
      positions[i * 3 + 1] = (Math.random() - 0.5) * 20;
      positions[i * 3 + 2] = (Math.random() - 0.5) * 20;

      velocities.push({
        x: (Math.random() - 0.5) * 0.02,
        y: (Math.random() - 0.5) * 0.02,
        z: (Math.random() - 0.5) * 0.02,
      });
    }

    geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));

    const canvasDot = document.createElement('canvas');
    canvasDot.width = 16;
    canvasDot.height = 16;
    const context = canvasDot.getContext('2d');
    if (context) {
      const gradient = context.createRadialGradient(8, 8, 0, 8, 8, 8);
      gradient.addColorStop(0, 'rgba(100, 180, 255, 1)');
      gradient.addColorStop(0.3, 'rgba(56, 139, 253, 0.7)');
      gradient.addColorStop(1, 'rgba(0, 0, 0, 0)');
      context.fillStyle = gradient;
      context.fillRect(0, 0, 16, 16);
    }

    const texture = new THREE.CanvasTexture(canvasDot);
    const material = new THREE.PointsMaterial({
      size: 0.38,
      map: texture,
      transparent: true,
      opacity: 0.8,
      depthWrite: false,
      blending: THREE.AdditiveBlending,
    });
    const particles = new THREE.Points(geometry, material);
    scene.add(particles);

    const maxConnections = 240;
    const linesGeometry = new THREE.BufferGeometry();
    const linePositions = new Float32Array(maxConnections * 2 * 3);
    linesGeometry.setAttribute('position', new THREE.BufferAttribute(linePositions, 3));

    const linesMaterial = new THREE.LineBasicMaterial({
      color: 0x388bfd,
      transparent: true,
      opacity: 0.14,
      blending: THREE.AdditiveBlending,
    });
    const lines = new THREE.LineSegments(linesGeometry, linesMaterial);
    scene.add(lines);

    const pointLight1 = new THREE.PointLight(0x00f0ff, 1.5, 50);
    pointLight1.position.set(-10, 10, 5);
    scene.add(pointLight1);

    const pointLight2 = new THREE.PointLight(0xff00d0, 1.5, 50);
    pointLight2.position.set(10, -10, 5);
    scene.add(pointLight2);

    this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    this.renderer.setSize(width, height);
    container.appendChild(this.renderer.domElement);

    let mouseX = 0;
    let mouseY = 0;

    this.mouseMoveHandler = (event: MouseEvent) => {
      const rect = container.getBoundingClientRect();
      const relativeX = event.clientX - rect.left;
      const relativeY = event.clientY - rect.top;
      mouseX = (relativeX / width) * 2 - 1;
      mouseY = -(relativeY / height) * 2 + 1;
    };
    container.addEventListener('mousemove', this.mouseMoveHandler);

    this.resizeObserver = new ResizeObserver((entries) => {
      for (const entry of entries) {
        width = entry.contentRect.width || container.clientWidth || 1;
        height = entry.contentRect.height || container.clientHeight || 1;

        camera.aspect = width / height;
        camera.updateProjectionMatrix();
        this.renderer?.setSize(width, height);
      }
    });
    this.resizeObserver.observe(container);

    const animate = () => {
      this.frame = requestAnimationFrame(animate);

      const positionAttr = geometry.getAttribute('position') as THREE.BufferAttribute;
      const array = positionAttr.array as Float32Array;

      for (let i = 0; i < particleCount; i++) {
        array[i * 3] += velocities[i].x;
        array[i * 3 + 1] += velocities[i].y;
        array[i * 3 + 2] += velocities[i].z;

        if (Math.abs(array[i * 3]) > 17) {
          velocities[i].x *= -1;
        }
        if (Math.abs(array[i * 3 + 1]) > 11) {
          velocities[i].y *= -1;
        }
        if (Math.abs(array[i * 3 + 2]) > 11) {
          velocities[i].z *= -1;
        }
      }

      camera.position.x += (mouseX * 5 - camera.position.x) * 0.05;
      camera.position.y += (mouseY * 3 - camera.position.y) * 0.05;
      camera.lookAt(scene.position);

      particles.rotation.y += 0.001;
      particles.rotation.x += 0.0004;

      let lineIndex = 0;
      let connectionCount = 0;
      const lineArray = linesGeometry.getAttribute('position').array as Float32Array;

      for (let i = 0; i < particleCount; i++) {
        for (let j = i + 1; j < particleCount; j++) {
          if (connectionCount >= maxConnections) {
            break;
          }

          const dx = array[i * 3] - array[j * 3];
          const dy = array[i * 3 + 1] - array[j * 3 + 1];
          const dz = array[i * 3 + 2] - array[j * 3 + 2];
          const distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

          if (distance < 4.2) {
            lineArray[lineIndex * 6] = array[i * 3];
            lineArray[lineIndex * 6 + 1] = array[i * 3 + 1];
            lineArray[lineIndex * 6 + 2] = array[i * 3 + 2];

            lineArray[lineIndex * 6 + 3] = array[j * 3];
            lineArray[lineIndex * 6 + 4] = array[j * 3 + 1];
            lineArray[lineIndex * 6 + 5] = array[j * 3 + 2];

            lineIndex++;
            connectionCount++;
          }
        }
      }

      linesGeometry.getAttribute('position').needsUpdate = true;
      lines.geometry.setDrawRange(0, connectionCount * 2);
      positionAttr.needsUpdate = true;
      this.renderer?.render(scene, camera);
    };
    animate();

    this.disposeScene = () => {
      if (this.mouseMoveHandler) {
        container.removeEventListener('mousemove', this.mouseMoveHandler);
      }
      this.resizeObserver?.disconnect();

      if (this.renderer?.domElement && container.contains(this.renderer.domElement)) {
        container.removeChild(this.renderer.domElement);
      }

      geometry.dispose();
      material.dispose();
      texture.dispose();
      linesGeometry.dispose();
      linesMaterial.dispose();
    };
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.frame);
    this.disposeScene?.();
    this.renderer?.dispose();
  }
}
